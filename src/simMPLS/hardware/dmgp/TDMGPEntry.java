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
package simMPLS.hardware.dmgp;

import simMPLS.protocols.TMPLSPDU;

/**
 * This class implements an entry of the DMGP memory. It stores a GoS packet and
 * all neccesary data to be forwarded.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TDMGPEntry implements Comparable {

    /**
     * This method is the constructor. It creates a new instance of TDMGPEntry
     * and initialize its attributes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingOrder Establish the incoming order in the global DMGP
     * memory.
     */
    public TDMGPEntry(int incomingOrder) {
        this.flowID = -1;
        this.packetID = -1;
        this.packet = null;
        this.order = incomingOrder;
    }

    /**
     * This method obtains the identifier of the flow associated to this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The identifier of the flow associated to this entry.
     * @since 1.0
     */
    public int getFlowID() {
        return this.flowID;
    }

    /**
     * This method obtains the identifier of the GoS packet stored in this
     * entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The identifier of the GoS packet.
     * @since 1.0
     */
    public int getPacketID() {
        return this.packetID;
    }

    /**
     * This method obtains the GoS packet that is stored in this entry of the
     * DMGP memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The stored GoS packet.
     * @since 1.0
     */
    public TMPLSPDU getPacket() {
        return this.packet.getAClon();
    }

    /**
     * This method insert the GoS packet in this entry of the DMGP memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param p The packet to be inserted in this entry.
     * @since 1.0
     */
    public void setPacket(TMPLSPDU p) {
        this.packet = p.getAClon();
        this.flowID = p.getIPv4Header().getOriginIP().hashCode();
        this.packetID = p.getIPv4Header().getGoSGlobalUniqueIdentifier();
    }

    /**
     * This method allow estabishing the order number in the complete DMGP
     * memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The order number of this DMGP entry.
     * @since 1.0
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * This method compares this DMGP entry with another to establish the oder.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param o DMGP entry to be compared with this one.
     * @return -1, 0, 1, depending on whether the current entry is lower, equal
     * or greater than the entry specified as argument. In terms of shorting.
     * @since 1.0
     */
    @Override
    public int compareTo(Object o) {
        TDMGPEntry dmgpEntry = (TDMGPEntry) o;
        if (this.order < dmgpEntry.getOrder()) {
            return TDMGPEntry.ESTE_MENOR;
        }
        if (this.order > dmgpEntry.getOrder()) {
            return TDMGPEntry.ESTE_MAYOR;
        }
        return TDMGPEntry.ESTE_IGUAL;
    }

    private static final int ESTE_MENOR = -1;
    private static final int ESTE_IGUAL = 0;
    private static final int ESTE_MAYOR = 1;

    private int flowID;
    private int packetID;
    private int order;
    private TMPLSPDU packet;
}
