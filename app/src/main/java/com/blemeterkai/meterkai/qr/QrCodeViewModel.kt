package com.blemeterkai.meterkai.qr

import android.graphics.Rect
import com.google.mlkit.vision.barcode.common.Barcode

/**
 * A ViewModel for encapsulating the data for a QR Code, including the encoded data, the bounding
 * box, and the touch behavior on the QR Code.
 *
 * As is, this class only handles displaying the QR Code data if it's a URL. Other data types
 * can be handled by adding more cases of Barcode.TYPE_URL in the init block.
 */
class QrCodeViewModel(barcode: Barcode) {
    var boundingRect: Rect = barcode.boundingBox!!
    var qrContent: String = ""

    init {
         qrContent = "${barcode.rawValue.toString()}"
    }
}