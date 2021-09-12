package ru.drsanches.life_together.utils

import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.apache.http.HttpEntity
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTOType
import java.text.DateFormat
import java.text.SimpleDateFormat

class Utils {

    static JSONObject findTransaction(JSONArray history, String userId, TransactionDTOType type, int money, String message) {
        for (JSONObject transaction: (history as List<JSONObject>)) {
            if (transaction["userId"] == userId
                    && transaction["type"] == type.name()
                    && transaction["amount"] == money
                    && transaction["message"] == message) {
                return transaction
            }
        }
        return null
    }

    static boolean checkTimestamp(Date dateBefore, String timestamp, Date dateAfter) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS z")
        Date date = df.parse(timestamp)
        return dateBefore.before(date) && dateAfter.after(date)
    }

    static Integer getAmount(JSONArray amounts, String userId) {
        for (JSONObject amount: (amounts as List<JSONObject>)) {
            if (amount["userId"] == userId) {
                return amount.getInt("amount")
            }
        }
        return null
    }

    static HttpEntity createMultipart(String filename) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
        File file = new File(filename)
        builder.addBinaryBody("file", new FileInputStream(file), ContentType.APPLICATION_OCTET_STREAM, file.getName())
        return builder.build()
    }

    static boolean checkImage(byte[] data, String filename) {
        File file = new File(filename)
        return data == file.getBytes()
    }

    static boolean checkImage(ByteArrayInputStream dataStream, String filename) {
        File file = new File(filename)
        return dataStream.getBytes() == file.getBytes()
    }

    static String getDefaultImagePath() {
        return "/api/v1/image/default"
    }
}