package com.example.orientacioeps.Entity;

import java.util.List;

/** @class Cami
 * @brief Conté els atributs propis d'un Camí
 * @author Genís Arumí Novellas
 */
public class Cami {
    public int id; ///< Id del Camí al servidor
    public List<Integer> cami; ///< Llista que conté les ids dels beacons que formen un Camí
}
