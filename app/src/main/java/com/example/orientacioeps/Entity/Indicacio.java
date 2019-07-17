package com.example.orientacioeps.Entity;

/** @class Indicacio
 * @brief Conté els atributs propis d'una Indicació
 * @author Genís Arumí Novellas
 */
public class Indicacio {
    public int origen; ///< Id del Beacon "origen"
    public int desti; ///< Id del Beacon "desti"
    public String missatge; ///< Missatge per indicar com arribar del beacon "origen" al beacon "desti"
}
