package tn.esprit.dentalcare.agendaclinique.domain;

/** CONFIRME -> DEPLACE | ANNULE | TERMINE | ABSENT. Seul un rendez-vous CONFIRME occupe un créneau. */
public enum StatutRdv {
    CONFIRME,
    DEPLACE,
    ANNULE,
    TERMINE,
    ABSENT
}
