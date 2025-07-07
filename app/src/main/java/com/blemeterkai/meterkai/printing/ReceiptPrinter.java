// In a new file: ReceiptPrinter.java
package com.blemeterkai.meterkai.printing; // Or your appropriate package

import static com.blemeterkai.meterkai.MainActivity.mPrintService;

import com.blemeterkai.meterkai.data.model.BillingData;
import com.google.gson.Gson; // If you need it here for other reasons
import com.woosim.printer.WoosimCmd;

import java.util.List;
import java.util.ArrayList;

// Assuming BillingData, DLMS, and d (if it's a class instance) are accessible
// or you pass necessary parts of them.

public class ReceiptPrinter {

    // You would add Bluetooth connection and printing methods here
    // For example:
    // private BluetoothDevice printerDevice;
    // private BluetoothSocket printerSocket;

    // public boolean connectToPrinter(String deviceAddress) { ... }
    // public void disconnectPrinter() { ... }
    // public boolean print(String textToPrint) { ... }

    public String formatReceipt(BillingData data, float[] total_value, String[] now_value, float[] ratio) {
        StringBuilder receiptContent = new StringBuilder();

        String title1 = "SAMPLE RECEIPT\n\n\n";
        String title2 = "           H.V Dela Costa St Salcedo Village Makati 1227,\n" +
                "          Metro Manila Philippines\n";
        String title3 = "        Fuji Electric Sales Philippines Inc.\n";
        String title4 = "                TEL:000-000-0000\n";

        receiptContent.append(title1);
        receiptContent.append(title2);
        receiptContent.append(title3);
        receiptContent.append(title4);

        String _str1 =
                "================================================================\n" +
                        /*期間 月(September) 年　　レートの種類:レート名　　　　*/
                        "Period     :%s       Rate Type     : %s COMMERCIAL\n";
        String str1 = String.format(_str1, data.Period, data.Commercial);
        receiptContent.append(str1);

        String _str2 =
                /*メーター：シリアル番号/契約番号？     乗数   */
                "Meter      :%s       Multiplier    :%1.01f\n" +
                        /*日時 MM/DD/YYYY 　　　　　　　　　　　　　　　　　　　　　　　　　今回検針値 6.3 */
                        "Period To  :%s                Pres Reading  : %6.03f\n";
        String str2 = String.format(_str2, data.SerialID, data.Multiplier, data.PeriodTo, data.PresReading);
        receiptContent.append(str2);

        String _str3 =
                /*日時 MM/DD/YYYY 　　　　　　　　　　　　　　　　　　前回検針値 6.3 */
                "Period From:%s                Prev Reading  : %6.03f\n" +
                        /*使用電力の瞬時値:2.3 　　　　　　　　　　　　　　　　　　         使用量 6.3 */
                        "Demand KW : %2.03f                  Total KWH Used  : %6.03f\n";
        String str3 = String.format(_str3, data.PeriodFrom, data.PrevReading, data.MaxDemand, data.TotalUse);
        receiptContent.append(str3);

        String str4 =
                "================================================================\n";
        receiptContent.append(str4);

        String str5 =
                "CHARGES                   RATE            AMOUNT\n" +
                        "GEN/TRANS CHARGES\n";
        receiptContent.append(str5);

        String _str6 =
                /*change name          　　　　　　　　　　　charge rate　　　rate*使用電力*/
                "  Generation System Charge    :       " + "%2.04f" + "/kwh" + "        %,6.02f\n" +
                        "  Transmission Demand Charge  :       " + "%4.02f" + "/kw " + "        %,6.02f\n" +
                        "  System Loss Charge          :       " + " %2.03f" + "/kwh" + "        %,6.02f\n";
        String str6 = String.format(_str6,
                ratio[0],
                total_value[0] * ratio[0],
                ratio[1],
                Float.parseFloat(now_value[3]) * ratio[1],
                ratio[2],
                total_value[0] * ratio[2]);
        receiptContent.append(str6);

        String _str7 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　　　　　　　　　　　　　　　　 GEN/TRANS CHARGESの小計 */
                        "                                       SUB TOTAL" + "        %,6.02f\n\n";
        String str7 = String.format(_str7, data.GenTransCharges);
        receiptContent.append(str7);

        String str8 =
                "DISTRIBUTION CHARGES\n";
        receiptContent.append(str8);

        String _str9 =
                /*change name          　　　　　　　　　　　charge rate　　　rate*使用電力*/
                "  Distribution Demand Charge  :       " + "%.02f" + "/kw " + "        %,6.02f\n" +
                        "  Supply Fix Charge           :       " + " %.02f" + "/cst" + "        %,6.02f\n" +
                        "  Metering Fix Charge         :       " + " %.02f" + "/cst" + "        %,6.02f\n";
        String str9 = String.format(_str9,
                ratio[3],
                Float.parseFloat(now_value[3]) * ratio[3],
                ratio[4],
                1 * ratio[4], // Consider making '1' a constant or clarify its meaning
                ratio[5],
                1 * ratio[5]); // Same here
        receiptContent.append(str9);

        String _str10 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　DISTRIBUTION CHARGESの小計 */
                        "                                       SUB TOTAL" + "        %,6.02f\n\n";
        String str10 = String.format(_str10, data.DistributionCharges);
        receiptContent.append(str10);

        String str11 =
                "REINVESTMENT FUND FOR\n" +
                        "SUSTAINABLE CAPEX\n";
        receiptContent.append(str11);

        String _str12 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Reinvestment Fund for CAPEX :       " + "%.04f" + "/kwh" + "        %,6.02f\n" +
                        "  Member's CAPEX Contribution :       " + "%.04f" + "/kwh" + "        %,6.02f\n";
        String str12 = String.format(_str12,
                ratio[6],
                total_value[0] * ratio[6],
                ratio[7],
                total_value[0] * ratio[7]);
        receiptContent.append(str12);

        String _str13 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　　　REINVESTMENT FUND FOR SUSTAINABLE CAPEXの小計 */
                        "                                       SUB TOTAL" + "        %,6.02f\n\n";
        String str13 = String.format(_str13, data.SustainableCapex);
        receiptContent.append(str13);

        String str14 =
                "OTHER CHARGES\n";
        receiptContent.append(str14);

        String _str15 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Lifeline Discount/Subsidy   :      " + "%.04f" + "/kwh" + "        %,6.02f\n" +
                        "  Senior Citizen Subsidy      :      " + " %.04f" + "/kwh" + "        %,6.02f\n";
        String str15 = String.format(_str15,
                ratio[8],
                total_value[0] * ratio[8],
                ratio[9],
                total_value[0] * ratio[9]);
        receiptContent.append(str15);

        String _str16 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　　　                          OTHER CHARGESの小計 */
                        "                                       SUB TOTAL" + "        %,6.02f\n\n";
        String str16 = String.format(_str16, data.OtherCharges);
        receiptContent.append(str16);

        String str17 =
                "UNIVERSAL CHARGES\n";
        receiptContent.append(str17);

        String _str18 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Missionary Elec(NPC-SPUG)   :       " + "%.04f" + "/kwh" + "        %,6.02f\n" +
                        "  Missionary Elec(RED)        :       " + "%.04f" + "/kwh" + "        %,6.02f\n" +
                        "  Environmental Charge        :       " + "%.04f" + "/kwh" + "        %,6.02f\n";
        String str18 = String.format(_str18,
                ratio[10],
                total_value[0] * ratio[10],
                ratio[11],
                total_value[0] * ratio[11], // This was total_value[3] * ratio[11] in original, verify if total_value[0] is correct
                ratio[12],
                total_value[0] * ratio[12]);
        receiptContent.append(str18);

        String _str19 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Feed In Tariff Allowance    :       " + "%.04f" + "/kwh" + "        %,6.02f\n" +
                        "  NPC Stranded Contract       :       " + "%.04f" + "/kwh" + "        %,6.02f\n" +
                        "  NPC Stranded Debts          :       " + "%.04f" + "/kwh" + "        %,6.02f\n";
        String str19 = String.format(_str19,
                ratio[13], total_value[0] * ratio[13],
                ratio[14], total_value[0] * ratio[14],
                ratio[15], total_value[0] * ratio[15]);
        receiptContent.append(str19);

        String _str20 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　　　UNIVERSAL CHARGESの小計 */
                        "                                       SUB TOTAL" + "        %,6.02f\n\n";
        String str20 = String.format(_str20, data.UniversalCharges);
        receiptContent.append(str20);

        String str21 =
                "VALUE ADDED TAX\n";
        receiptContent.append(str21);

        String _str22 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Generation VAT              :      " + " %.04f" + "/kwh" + "        %,6.02f\n" +
                        "  Transmission VAT            :      " + " %.04f" + "/kwh" + "        %,6.02f\n" +
                        "  System Loss VAT             :      " + " %.04f" + "/kwh" + "        %,6.02f\n";
        String str22 = String.format(_str22,
                ratio[16],
                total_value[0] * ratio[16],
                ratio[17], total_value[0] * ratio[17],
                ratio[18], total_value[0] * ratio[18]);
        String _str23 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Distribution VAT            :         " + " %.04f" + "%%" + "        %,6.02f\n" +
                        "  Other VAT                   :         " + " %.04f" + "%%" + "        %,6.02f\n";
        String str23 = String.format(_str23,
                ratio[19],
                total_value[2] * ratio[19],
                ratio[20],
                total_value[4] * ratio[20]);
        String _str24 =
                "                                                ----------------\n" +
                        /*VALUE ADDED TAXの小計 */
                        "                                       SUB TOTAL" + "        %,6.02f\n\n";
        String str24 = String.format(_str24, data.ValueAddedTax);

        String str25 =
                "----------------------------------------------------------------\n";
        String _str26 =
                /*現在の請求額*/
                "CURRENT BILL                                       Php" + " %,6.02f\n";
        String str26 = String.format(_str26, total_value[7]);
        String _str27 =
                /*各小計の合計の請求額*/
                "TOTAL AMOUNT                       Php" + " %,6.02f\n";
        String str27 = String.format(_str27, data.TotalAmount);
        String str28 =
                "================================================================\n";
        String _str29 =
                /*値引額*/
                "Discount                              " + "  %,6.02f\n";
        String str29 = String.format(_str29, data.Discount);
        String _str30 =
                /*合計の請求額から値引きされた金額*/
                "Amount Before Due                   " + "  %,6.02f\n\n";
        String str30 = String.format(_str30, total_value[8]);
        String _str31 =
                /*利息額*/
                "Interest                              " + "  %,6.02f\n";
        String str31 = String.format(_str31, data.Interest);
        String _str32 =
                /*合計の請求額から利息額が追加された金額*/
                "Amount After Due                    " + "  %,6.02f\n\n";
        String str32 = String.format(_str32, total_value[9]);

        String _str33 =
                /*支払い期日　           月(Oct)　dd,yyyy　*/
                "     DUE DATE     :" + "%s\n" +
                        "     DISCO DATE   :" + "%s\n\n";
        String str33 = String.format(_str33, data.DueDate, data.DiscoDate);
        String str34 =
                "NOTE:Please pay this electric bill on or before DUE DATE otherwise,\n" +
                        "     we will be forced to discontinue serving your electric needs.\n\n";
        String str35 =
                "This is not an Official Receipt. Payment of this bill does not mean \n" +
                        "payment of previous delinquencies if any.\n\n";
        String _str36 =
                "             **PLEASE PRESENT THIS STATEMENT UPON PAYMENT**\n" +
                        /*検針担当：名前 　　　　　　検診日時 曜日(Thu) dd 月(Oct) yyyy　HH:mm:ss */
                        "Reader:%s                   " + "%s\n\n";
        String str36 = String.format(_str36, data.Reader, data.ReadDatetime);
        String _str37 =
                /*フォーマットのバージョン*/
                "Version : %s\n\n\n\n";
        String str37 = String.format(_str37, data.Version);

        mPrintService.write(WoosimCmd.initPrinter());
        if (false) {
//        mPrintService.write(WoosimCmd.setPageMode());
//        mPrintService.write(WoosimCmd.PM_setArea(0, 0, 600, 9000));
//        mPrintService.write(WoosimCmd.PM_setArea(0, 0, 600, 9000));
        }
        mPrintService.write(WoosimCmd.PM_setPosition(0, 0));
        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
        mPrintService.write(title1.getBytes());
        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
        mPrintService.write(title2.getBytes());
        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 2));
        mPrintService.write(title3.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(WoosimCmd.setTextStyle(false, false, false, 1, 1));
        mPrintService.write(title4.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str1.getBytes());
        mPrintService.write(str2.getBytes());
        mPrintService.write(str3.getBytes());
        mPrintService.write(str4.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
        mPrintService.write(str5.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str6.getBytes());
        mPrintService.write(str7.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
        mPrintService.write(str8.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str9.getBytes());
        mPrintService.write(str10.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
        mPrintService.write(str11.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str12.getBytes());
        mPrintService.write(str13.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
        mPrintService.write(str14.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str15.getBytes());
        mPrintService.write(str16.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
        mPrintService.write(str17.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str18.getBytes());
        mPrintService.write(str19.getBytes());
        mPrintService.write(str20.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
        mPrintService.write(str21.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str22.getBytes());
        mPrintService.write(str23.getBytes());
        mPrintService.write(str24.getBytes());
        mPrintService.write(str25.getBytes());
        mPrintService.write(str26.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 2));
        mPrintService.write(str27.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str28.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
        mPrintService.write(str29.getBytes());
        mPrintService.write(str30.getBytes());
        mPrintService.write(str31.getBytes());
        mPrintService.write(str32.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
        mPrintService.write(str33.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_SMALL));
        mPrintService.write(str34.getBytes());
        mPrintService.write(str35.getBytes());
        mPrintService.write(str36.getBytes());
        mPrintService.write(str37.getBytes());
        mPrintService.write(WoosimCmd.PM_printStdMode());
        return receiptContent.toString();
    }
}