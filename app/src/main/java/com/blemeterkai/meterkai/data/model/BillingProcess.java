package com.blemeterkai.meterkai.data.model;
// BillingProcessor.java
import com.blemeterkai.meterkai.MainActivity;
import com.blemeterkai.meterkai.dlms.DLMS;
import com.blemeterkai.meterkai.printing.ReceiptPrinter;
import com.google.gson.Gson;

import java.io.File; // Assuming folderExternal is a File object
import java.util.ArrayList;
import java.util.List;
// Add other necessary imports: Gson, DLMS, BillingData, ReceiptPrinter etc.

public class BillingProcess {

    // You might need to pass ratio, d (DLMS instance?), and folderExternal
    // to this class's constructor or as method parameters if they are not globally accessible.
    // For this example, I'll assume they are accessible.

    public static void processBillingData(final String[] now_value, final String[] old_value, final boolean withPrinting, final float[] ratio, final DLMS d, final File folderExternal) {

        float[] total_value = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        total_value[0] =
                Float.parseFloat(now_value[2]) - Float.parseFloat(old_value[1]);
        total_value[1] =
                total_value[0] * ratio[0] +
                        Float.parseFloat(now_value[3]) * ratio[1] +
                        total_value[0] * ratio[2];
        total_value[2] =
                Float.parseFloat(now_value[3]) * ratio[3] +
                        1 * ratio[4] +
                        1 * ratio[5];
        total_value[3] =
                total_value[0] * ratio[6] +
                        total_value[0] * ratio[7];
        total_value[4] =
                total_value[0] * ratio[8] + total_value[0] * ratio[9];
        total_value[5] =
                total_value[0] * MainActivity.ratio[10] +
                        total_value[3] * ratio[11] +
                        total_value[0] * ratio[12] +
                        total_value[0] * ratio[13] +
                        total_value[0] * ratio[14] +
                        total_value[0] * ratio[15];
        total_value[6] =
                total_value[0] * ratio[16] +
                        total_value[0] * ratio[17] +
                        total_value[0] * ratio[18] +
                        total_value[2] * ratio[19] +
                        total_value[4] * ratio[20];
        total_value[7] =
                total_value[1] +
                        total_value[2] +
                        total_value[3] +
                        total_value[4] +
                        total_value[5] +
                        total_value[6];

        BillingData data = new BillingData();
        data.Period = DLMS.dateTimeToMonth(old_value[0]);
        data.Commercial = "LARGE";
        data.SerialID = old_value[2];
        data.Multiplier = 1.0f;
        data.PeriodFrom = d.ConvertLocalDatetime(old_value[0]);
        data.PeriodTo = d.ConvertLocalDatetime(now_value[0]);
        data.PrevReading = Float.parseFloat(old_value[1]);
        data.PresReading = Float.parseFloat(now_value[2]);
        data.MaxDemand = Float.parseFloat(now_value[3]);
        data.TotalUse = total_value[0];
        data.GenTransCharges = trimFloat(total_value[1]);
        data.DistributionCharges = trimFloat(total_value[2]);
        data.SustainableCapex = trimFloat(total_value[3]);
        data.OtherCharges = trimFloat(total_value[4]);
        data.UniversalCharges = trimFloat(total_value[5]);
        data.ValueAddedTax = trimFloat(total_value[6]);
        data.TotalAmount = trimFloat(total_value[7]);
        data.DueDate = d.FormattedMonthDay(1, 0);
        data.DiscoDate = d.FormattedMonthDay(1, 1);
        data.Discount = 10.0f;
        data.Interest = 10.0f;
        total_value[8] =
                data.TotalAmount - data.Discount;
        total_value[9] =
                data.TotalAmount + data.Interest;
        data.Reader = "Fuji Taro";
        data.ReadDatetime = DLMS.getNowDate();
        data.Version = "v1.00.2";
        data.Period = DLMS.dateTimeToMonth(old_value[0]);
        data.Commercial = "LARGE";

        Gson gson = new Gson();
        List<BillingData> outputlist = new ArrayList<>();
        outputlist.add(data);
        String name = d.CurrentYearMonth() + "_" + data.SerialID + ".json";
        writeFile(gson.toJson(outputlist), name, folderExternal); // You'll need to move or make writeFile accessible
        System.out.println(gson.toJson(outputlist));
        if (withPrinting) {
            ReceiptPrinter printer = new ReceiptPrinter();
            printer.formatReceipt(data, total_value, now_value, ratio);
        }
    }

    // If writeFile is a helper method in MainActivity, you'll need to move it here too,
    // or make it a public static method in a utility class.
    private static void writeFile(String data, String name, File folder) {
        // ... (implementation of writeFile)
    }

    // Similarly, move trimFloat or make it accessible
    private static float trimFloat(final float in) {
        String str = String.format("%.02f", in);
        return Float.parseFloat(str);
    }
}
