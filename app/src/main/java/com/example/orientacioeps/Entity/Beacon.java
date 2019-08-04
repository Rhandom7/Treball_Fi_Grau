package com.example.orientacioeps.Entity;

import java.util.List;

/** @class Beacon
 * @brief Conté els atributs propis d'un Beacon
 * @author Genís Arumí Novellas
 */
public class Beacon {
    public int id; ///< Identificador del beacon al servidor
    public String codi; ///< Id pròpia del beacon
    public int pis; ///< Pis on està el beacon
    public String edifici; ///< Edifici on es troba el beacon
    public List<Integer> espais; ///< Espais/destinacions que té aprop el beacon
}
