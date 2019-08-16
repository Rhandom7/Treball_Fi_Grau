package com.example.orientacioeps.Entity;

/** @class Indicacio
 * @brief Conté els atributs propis d'una Indicació
 * @author Genís Arumí Novellas
 */
public class Indicacio {
    public int id; ///< Id de la Indicació
    public Beacon origen; ///< Beacon "origen"
    public Beacon desti; ///< Beacon "desti"
    public String missatge; ///< Missatge per indicar com arribar del beacon "origen" al beacon "desti"
}
