package edu.upenn.cis.precise.openicelite.iomt.medical.masimo.rad8;

import java.nio.charset.Charset;

/**
 * This class contains constants for the MasimoData specification
 *
 * @author Pruthvi Hanumanthapura Ramakrishna (hrama@seas.upenn.edu)
 */
public class MasimoConstants {
    public static final Charset CHARSET = Charset.forName("ASCII");

    public static final int EXC = 10;
    public static final int ALARM = 10;
    public static final int PIDELTA = 11;
    public static final int DESAT = 8;
    public static final int SPMET1 = 9;
    public static final int SPMET2 = 11;
    public static final int SPCO = 10;
    public static final int PI = 9;
    public static final int BPM = 7;
    public static final int SPO2 = 9;
    public static final int SN = 13;
    public static final int START_DATE = 8;
    public static final int START_TIME = 8;
    public static final int SEPARATOR = 1;

    public static final int TOTAL = EXC +ALARM + PIDELTA + DESAT + SPMET2 + SPCO + SPO2 + PI + BPM
            + SN + START_DATE + START_TIME;
}
