package edu.upenn.cis.precise.openicelite.iomt.medical.masimo.rad8;

/**
 * @author Pruthvi Hanumanthapura Ramakrishna (hrama@seas.upenn.edu)
 */
public class MasimoRecord {
    private String loadDate;            //eight bytes
    private String loadTime;
    private String SN;                  //ten bytes
    private short SPO2;                 //three bytes
    private short BPM;                  //three bytes
    private short PI;                   //two decimal before and after
    private short SPCO;                 //two decimal before and one after
    private short SPMET;                //two decimal before and one after
    private short DESAT;                //two bytes
    private short PIDELTA;              //three bytes starting with +
    private String ALARM;               //four bytes
    private String EXC;                 //six bytes

    public MasimoRecord() {
    }

    public String getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(String loadDate) {
        this.loadDate = loadDate;
    }

    public String getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String sN) {
        SN = sN;
    }

    public short getSPO2() {
        return SPO2;
    }

    public void setSPO2(String sPO2) {
        if (sPO2.equals("---%"))
            SPO2 = -1;
        else
            SPO2 = (short) Integer.parseInt(sPO2.substring(0, sPO2.length() - 1));
    }

    public short getBPM() {
        return BPM;
    }

    public void setBPM(String bPM) {
        if (bPM.equals("---"))
            BPM = -1;
        else
            BPM = (short) Integer.parseInt(bPM);
    }

    public short getPI() {
        return PI;
    }

    public void setPI(String pI) {
        if (pI.equals("--.--%"))
            PI = -1;
        else
            PI = (short) Double.parseDouble(pI.substring(0, pI.length() - 1));
    }

    public short getSPCO() {
        return SPCO;
    }

    public void setSPCO(String sPCO) {
        if (sPCO.equals("--.-%"))
            SPCO = -1;
        else
            SPCO = (short) Double.parseDouble(sPCO.substring(0, sPCO.length() - 1));
    }

    public short getSPMET() {
        return SPMET;
    }

    public void setSPMET(String sPMET) {
        if (sPMET.equals("--.-%") || sPMET.equals("--."))
            SPMET = -1;
        else
            SPMET = (short) Double.parseDouble(sPMET.substring(0, sPMET.length() - 1));
    }

    public short getDESAT() {
        return DESAT;
    }

    public void setDESAT(String dESAT) {
        if (dESAT.equals("--"))
            DESAT = -1;
        else
            DESAT = (short) Integer.parseInt(dESAT);
    }

    public short getPIDELTA() {
        return PIDELTA;
    }

    public void setPIDELTA(String pIDELTA) {
        if (pIDELTA.equals("+--"))
            PIDELTA = -1;
        else
            PIDELTA = (short) Integer.parseInt(pIDELTA.substring(1, pIDELTA.length()));
    }

    public String getALARM() {
        return ALARM;
    }

    public void setALARM(String aLARM) {
        ALARM = aLARM;
    }

    public String getEXC() {
        return EXC;
    }

    public void setEXC(String eXC) {
        EXC = eXC;
    }

    @Override
    public String toString() {
        return "Data [loaddate=" + loadDate + ", loadtime=" + loadTime +", SN=" + SN
                + ", SPO2=" + SPO2 + ", BPM=" + BPM + ", PI=" + PI + ", SPCO=" + SPCO
                + ", SPMET=" + SPMET + ", DESAT=" + DESAT + ", PIDELTA=" + PIDELTA
                + ", ALARM=" + ALARM + ", EXC=" + EXC + "]";
    }
}
