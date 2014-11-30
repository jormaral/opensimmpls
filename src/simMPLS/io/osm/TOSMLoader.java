/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.io.osm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.CRC32;
import simMPLS.scenario.TInternalLink;
import simMPLS.scenario.TExternalLink;
import simMPLS.scenario.TScenario;
import simMPLS.scenario.TLERNode;
import simMPLS.scenario.TSenderNode;
import simMPLS.scenario.TReceiverNode;
import simMPLS.scenario.TActiveLSRNode;
import simMPLS.scenario.TLSRNode;
import simMPLS.scenario.TActiveLERNode;


/**
 * This class implements a class that loads a scenario from disk in OSM (Open
 * SimMPLS format).
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TOSMLoader {
    
    /**
     * Crea una nueva instancia de TCargadorOSM
     * @since 1.0
     */
    public TOSMLoader() {
        this.scenario = new TScenario();
        this.inputStream = null;
        this.input = null;
        this.scenarioCRC = new CRC32();
        this.position = TOSMLoader.NONE;
}
    
    /**
     * Este m�todo carga desde el disco, del fichero especificado, un scenario.
     * @param inputFile El fichero de disco que el cargador debe leer para crear en memoria el
 scenario.
     * @return TRUE, si el scenario se ha cargado correctamente. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean cargar(File inputFile) {
        if (this.fileIsValid(inputFile)) {
            String stringAux = "";
            this.scenario.setFile(inputFile);
            this.scenario.setSaved(true);
            this.scenario.setModified(false);
            try {
                if(inputFile.exists()) {
                    this.inputStream = new FileInputStream(inputFile);
                    this.input = new BufferedReader(new InputStreamReader(this.inputStream));
                    while ((stringAux = this.input.readLine()) != null) {
                        if ((!stringAux.equals("")) && (!stringAux.startsWith("//")) && (!stringAux.startsWith("@CRC#"))) {
                            if (this.position == TOSMLoader.NONE) {
                                if (stringAux.startsWith("@?Escenario")) {
                                    this.position = TOSMLoader.SCENARIO;
                                } else if (stringAux.startsWith("@?Topologia")) {
                                    this.position = TOSMLoader.TOPOLOGY;
                                } else if (stringAux.startsWith("@?Simulacion")) {
                                    this.position = TOSMLoader.SIMULATION;
                                } else if (stringAux.startsWith("@?Analisis")) {
                                    this.position = TOSMLoader.ANALISYS;
                                } 
                            } else if (position == TOSMLoader.SCENARIO) {
                                cargarEscenario(stringAux);
                            } else if (position == TOSMLoader.TOPOLOGY) {
                                loadTopology(stringAux);
                            } else if (position == TOSMLoader.SIMULATION) {
                                if (stringAux.startsWith("@!Simulacion")) {
                                    this.position = TOSMLoader.NONE;
                                }
                            } else if (position == TOSMLoader.ANALISYS) {
                                if (stringAux.startsWith("@!Analisis")) {
                                    this.position = TOSMLoader.NONE;
                                } 
                            }
                        }
                    }
                    this.inputStream.close();
                    this.input.close();
                }
            }
            catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    private void loadTopology(String topologyString) {
        if (topologyString.startsWith("@!Topologia")) {
            this.position = TOSMLoader.NONE;
        } else if (topologyString.startsWith("#Receptor#")) {
            TReceiverNode receiver = new TReceiverNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (receiver.unmarshall(topologyString)) {
                this.scenario.getTopology().addNode(receiver);
                this.scenario.getTopology().getItemIdentifierGenerator().setIDIfGreater(receiver.getID());
                this.scenario.getTopology().getIPAddressGenerator().setValueIfGreater(receiver.getIPAddress());
            }
            receiver = null;
        } else if (topologyString.startsWith("#Emisor#")) {
            TSenderNode sender = new TSenderNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (sender.unmarshall(topologyString)) {
                this.scenario.getTopology().addNode(sender);
                this.scenario.getTopology().getItemIdentifierGenerator().setIDIfGreater(sender.getID());
                this.scenario.getTopology().getIPAddressGenerator().setValueIfGreater(sender.getIPAddress());
            }
            sender = null;
        } else if (topologyString.startsWith("#LER#")) {
            TLERNode ler = new TLERNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (ler.unmarshall(topologyString)) {
                this.scenario.getTopology().addNode(ler);
                this.scenario.getTopology().getItemIdentifierGenerator().setIDIfGreater(ler.getID());
                this.scenario.getTopology().getIPAddressGenerator().setValueIfGreater(ler.getIPAddress());
            }
            ler = null;
        } else if (topologyString.startsWith("#LERA#")) {
            TActiveLERNode activeLER = new TActiveLERNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (activeLER.unmarshall(topologyString)) {
                this.scenario.getTopology().addNode(activeLER);
                this.scenario.getTopology().getItemIdentifierGenerator().setIDIfGreater(activeLER.getID());
                this.scenario.getTopology().getIPAddressGenerator().setValueIfGreater(activeLER.getIPAddress());
            }
            activeLER = null;
        } else if (topologyString.startsWith("#LSR#")) {
            TLSRNode lsr = new TLSRNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (lsr.unmarshall(topologyString)) {
                this.scenario.getTopology().addNode(lsr);
                this.scenario.getTopology().getItemIdentifierGenerator().setIDIfGreater(lsr.getID());
                this.scenario.getTopology().getIPAddressGenerator().setValueIfGreater(lsr.getIPAddress());
            }
            lsr = null;
        } else if (topologyString.startsWith("#LSRA#")) {
            TActiveLSRNode activeLSR = new TActiveLSRNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (activeLSR.unmarshall(topologyString)) {
                this.scenario.getTopology().addNode(activeLSR);
                this.scenario.getTopology().getItemIdentifierGenerator().setIDIfGreater(activeLSR.getID());
                this.scenario.getTopology().getIPAddressGenerator().setValueIfGreater(activeLSR.getIPAddress());
            }
            activeLSR = null;
        } else if (topologyString.startsWith("#EnlaceExterno#")) {
            TExternalLink externalLink = new TExternalLink(0, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (externalLink.unmarshall(topologyString)) {
                this.scenario.getTopology().addLink(externalLink);
                this.scenario.getTopology().getItemIdentifierGenerator().setIDIfGreater(externalLink.getID());
            }
            externalLink = null;
        } else if (topologyString.startsWith("#EnlaceInterno#")) {
            TInternalLink internalLink = new TInternalLink(0, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (internalLink.unmarshall(topologyString)) {
                this.scenario.getTopology().addLink(internalLink);
                this.scenario.getTopology().getItemIdentifierGenerator().setIDIfGreater(internalLink.getID());
            }
            internalLink = null;
        }
    }

    private void cargarEscenario(String cadena) {
        if (cadena.startsWith("@!Escenario")) {
            this.position = TOSMLoader.NONE;
        } else if (cadena.startsWith("#Titulo#")) {
            if (!this.scenario.deserializarTitulo(cadena)) {
                this.scenario.ponerTitulo("");
            }
        } else if (cadena.startsWith("#Autor#")) {
            if (!this.scenario.deserializarAutor(cadena)) {
                this.scenario.ponerAutor("");
            }
        } else if (cadena.startsWith("#Descripcion#")) {
            if (!this.scenario.deserializarDescripcion(cadena)) {
                this.scenario.ponerDescripcion("");
            }
        } else if (cadena.startsWith("#Temporizacion#")) {
            if (!this.scenario.obtenerSimulacion().deserializarParametrosTemporales(cadena)) {
                this.scenario.obtenerSimulacion().ponerDuracion(500);
                this.scenario.obtenerSimulacion().ponerPaso(1);
            }
        }
    }
    
    /**
     * Este m�todo devuelve el scenario que el cargador ha creado en memoria tras leer
 elfichero asociado en disco.
     * @return El scenario correctamente creado en memoria.
     * @since 1.0
     */    
    public TScenario obtenerEscenario() {
        return this.scenario;
    }
    
    private boolean fileIsValid(File f) {
        String CRCDelFichero = "";
        String CRCCalculado = "@CRC#";
        this.scenarioCRC.reset();
        String cadena = "";
        try {
            if(f.exists()) {
                FileInputStream fEntrada = new FileInputStream(f);
                BufferedReader ent = new BufferedReader(new InputStreamReader(fEntrada));
                while ((cadena = ent.readLine()) != null) {
                    if ((!cadena.equals("")) && (!cadena.startsWith("//"))) {
                        if (cadena.startsWith("@CRC#")) {
                            CRCDelFichero = cadena;
                        } else {
                            this.scenarioCRC.update(cadena.getBytes());
                        }
                    }
                }
                fEntrada.close();
                ent.close();
                if (CRCDelFichero.equals("")) {
                    return true;
                } else {
                    CRCCalculado += Long.toString(this.scenarioCRC.getValue());
                    if (CRCCalculado.equals(CRCDelFichero)) {
                        return true;
                    }
                    return false;
                }
            }
        }
        catch (IOException e) {
            this.scenarioCRC.reset();
            return false;
        }
        this.scenarioCRC.reset();
        return false;
    }
    
    private static final int NONE = 0;
    private static final int SCENARIO = 1;
    private static final int TOPOLOGY = 2;
    private static final int SIMULATION = 3;
    private static final int ANALISYS = 4;

    private int position;
    private CRC32 scenarioCRC;
    private TScenario scenario;
    private FileInputStream inputStream;
    private BufferedReader input;
}