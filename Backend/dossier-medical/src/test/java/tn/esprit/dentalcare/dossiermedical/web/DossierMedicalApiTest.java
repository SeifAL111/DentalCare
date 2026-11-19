package tn.esprit.dentalcare.dossiermedical.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'intégration des endpoints (base H2 dédiée, sans Eureka ni données de démo).
 */
@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "dossier.seed.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:dossier_medical_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"
})
class DossierMedicalApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void preparer() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    // ---------- Dossier ----------

    @Test
    void creerDossier_puisDoublon_renvoie409() throws Exception {
        String patientId = nouveauPatient();
        mvc.perform(post("/api/dossiers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"patientId\":\"" + patientId + "\",\"groupeSanguin\":\"A+\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.statutAllergies").value("NON_RENSEIGNE"));

        mvc.perform(post("/api/dossiers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"patientId\":\"" + patientId + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflit"));
    }

    @Test
    void donneesInvalides_renvoient400AvecLeDetailDesChamps() throws Exception {
        mvc.perform(post("/api/dossiers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"patientId\":\"\",\"groupeSanguin\":\"Z+\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreurs.patientId").exists())
                .andExpect(jsonPath("$.erreurs.groupeSanguin").exists());
    }

    @Test
    void dossierInexistant_renvoie404AuFormatProblemDetail() throws Exception {
        mvc.perform(get("/api/dossiers/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Ressource introuvable"));
    }

    @Test
    void consultationParPatientId() throws Exception {
        String patientId = nouveauPatient();
        long id = creerDossier(patientId);
        mvc.perform(get("/api/dossiers/patient/" + patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void modification_avecVersionObsolete_renvoie409() throws Exception {
        long id = creerDossier(nouveauPatient());

        mvc.perform(put("/api/dossiers/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":0,\"groupeSanguin\":\"O-\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupeSanguin").value("O-"))
                .andExpect(jsonPath("$.version").value(1));

        mvc.perform(put("/api/dossiers/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":0,\"groupeSanguin\":\"B+\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void archivage_rendLeDossierInvisible() throws Exception {
        long id = creerDossier(nouveauPatient());
        mvc.perform(delete("/api/dossiers/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/dossiers/" + id)).andExpect(status().isNotFound());
    }

    // ---------- Allergies ----------

    @Test
    void allergie_cycleDeVie_etStatutDesAllergies() throws Exception {
        long id = creerDossier(nouveauPatient());
        long allergieId = ajouterAllergie(id, "Pénicilline", "ANAPHYLAXIE");

        mvc.perform(get("/api/dossiers/" + id))
                .andExpect(jsonPath("$.statutAllergies").value("RENSEIGNEES"))
                .andExpect(jsonPath("$.allergies", hasSize(1)))
                .andExpect(jsonPath("$.allergies[0].famillesReconnues[0]").value("PENICILLINES"));

        mvc.perform(patch("/api/dossiers/" + id + "/declarer-aucune-allergie"))
                .andExpect(status().isConflict());

        mvc.perform(delete("/api/dossiers/" + id + "/allergies/" + allergieId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/dossiers/" + id))
                .andExpect(jsonPath("$.statutAllergies").value("NON_RENSEIGNE"));

        mvc.perform(patch("/api/dossiers/" + id + "/declarer-aucune-allergie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statutAllergies").value("AUCUNE_CONNUE"));
    }

    @Test
    void allergieDunAutreDossier_estIntrouvable() throws Exception {
        long dossierA = creerDossier(nouveauPatient());
        long dossierB = creerDossier(nouveauPatient());
        long allergieDeA = ajouterAllergie(dossierA, "Latex", "MODEREE");

        mvc.perform(get("/api/dossiers/" + dossierB + "/allergies/" + allergieDeA))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/dossiers/" + dossierB + "/allergies/" + allergieDeA))
                .andExpect(status().isNotFound());
    }

    // ---------- Contrôles médicaux ----------

    @Test
    void contreIndication_penicillineEtAmoxicilline_estBloquante() throws Exception {
        String patientId = nouveauPatient();
        long id = creerDossier(patientId);
        ajouterAllergie(id, "Pénicilline", "ANAPHYLAXIE");

        mvc.perform(post("/api/dossiers/" + id + "/contre-indications/verifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"medicament\":\"Amoxicilline 1 g\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.niveau").value("BLOQUANT"))
                .andExpect(jsonPath("$.raisons[0].type").value("ALLERGIE"));

        mvc.perform(post("/api/dossiers/patient/" + patientId + "/contre-indications/verifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"medicament\":\"Paracétamol 1 g\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.niveau").value("OK"));
    }

    @Test
    void facteursRisque_dependentDeLActe() throws Exception {
        String patientId = nouveauPatient();
        long id = creerDossier(patientId);
        mvc.perform(post("/api/dossiers/" + id + "/antecedents").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"TRAITEMENT_EN_COURS","description":"Apixaban 5 mg",
                                 "facteurRisque":"ANTICOAGULANT"}"""))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/dossiers/patient/" + patientId + "/facteurs-risque").param("acte", "EXTRACTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.niveauGlobal").value("ROUGE"))
                .andExpect(jsonPath("$.protocoleRenforce").value(true));

        mvc.perform(get("/api/dossiers/" + id + "/facteurs-risque").param("acte", "DETARTRAGE"))
                .andExpect(jsonPath("$.niveauGlobal").value("VERT"))
                .andExpect(jsonPath("$.protocoleRenforce").value(false));
    }

    // ---------- Consentements ----------

    @Test
    void consentement_obligatoireAvantActeInvasif_puisRevocation() throws Exception {
        long id = creerDossier(nouveauPatient());
        String verifier = "/api/dossiers/" + id + "/consentements/verifier";

        mvc.perform(get(verifier).param("acte", "IMPLANT"))
                .andExpect(jsonPath("$.autorise").value(false));
        mvc.perform(get(verifier).param("acte", "DETARTRAGE"))
                .andExpect(jsonPath("$.autorise").value(true));

        String json = signerConsentement(id, "IMPLANT")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("SIGNE"))
                .andReturn().getResponse().getContentAsString();
        long consentementId = ((Number) JsonPath.read(json, "$.id")).longValue();

        mvc.perform(get(verifier).param("acte", "IMPLANT"))
                .andExpect(jsonPath("$.autorise").value(true));
        signerConsentement(id, "IMPLANT").andExpect(status().isConflict());

        mvc.perform(get("/api/dossiers/" + id + "/consentements/" + consentementId + "/integrite"))
                .andExpect(jsonPath("$.integre").value(true));

        String revoquer = "/api/dossiers/" + id + "/consentements/" + consentementId + "/revoquer";
        mvc.perform(patch(revoquer).contentType(MediaType.APPLICATION_JSON).content("{\"motif\":\"Réflexion\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REVOQUE"));
        mvc.perform(patch(revoquer)).andExpect(status().isConflict());

        mvc.perform(get(verifier).param("acte", "IMPLANT"))
                .andExpect(jsonPath("$.autorise").value(false));
    }

    // ---------- Partage ----------

    @Test
    void partage_limite_journalise_puisExpire() throws Exception {
        long id = creerDossier(nouveauPatient());
        ajouterAllergie(id, "Latex", "MODEREE");

        String json = mvc.perform(post("/api/dossiers/" + id + "/partages").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"praticienId":"praticien-benali","dureeHeures":2,
                                 "portee":"ALLERGIES","nbConsultationsMax":1}"""))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(json, "$.token");
        String lien = "/api/dossiers/partages/" + token;

        mvc.perform(get(lien).param("praticienId", "praticien-intrus"))
                .andExpect(status().isForbidden());

        mvc.perform(get(lien).param("praticienId", "praticien-benali"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allergies", hasSize(1)))
                .andExpect(jsonPath("$.antecedents").doesNotExist())
                .andExpect(jsonPath("$.consultationsRestantes").value(0));

        mvc.perform(get(lien).param("praticienId", "praticien-benali"))
                .andExpect(status().isGone());

        mvc.perform(get("/api/dossiers/" + id + "/journal-acces"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].praticienId").value("praticien-benali"));

        mvc.perform(get("/api/dossiers/partages/jeton-inconnu").param("praticienId", "praticien-benali"))
                .andExpect(status().isNotFound());
    }

    // ---------- Outils ----------

    private static String nouveauPatient() {
        return "patient-" + UUID.randomUUID();
    }

    private long creerDossier(String patientId) throws Exception {
        String json = mvc.perform(post("/api/dossiers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"patientId\":\"" + patientId + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }

    private long ajouterAllergie(long dossierId, String substance, String severite) throws Exception {
        String json = mvc.perform(post("/api/dossiers/" + dossierId + "/allergies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"substance\":\"%s\",\"categorie\":\"MEDICAMENT\",\"severite\":\"%s\"}"
                                .formatted(substance, severite)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }

    private ResultActions signerConsentement(long dossierId, String acte) throws Exception {
        return mvc.perform(post("/api/dossiers/" + dossierId + "/consentements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"typeActe":"%s","signataire":"Patient Test","praticienId":"praticien-moreau",
                         "versionDocument":"v1","texteInforme":"Information complète sur l'acte et ses risques."}"""
                        .formatted(acte)));
    }
}
