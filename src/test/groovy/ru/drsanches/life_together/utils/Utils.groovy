package ru.drsanches.life_together.utils

import net.sf.json.JSONArray
import net.sf.json.JSONNull
import net.sf.json.JSONObject
import org.apache.http.HttpEntity
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTOType
import java.text.DateFormat
import java.text.SimpleDateFormat

class Utils {

    static JSONObject findTransaction(JSONArray history, String userId, TransactionDTOType type, int money, String message) {
        def messageResponse = message == null ? JSONNull.getInstance() : message
        for (JSONObject transaction: (history as List<JSONObject>)) {
            if (transaction["userId"] == userId
                    && transaction["type"] == type.name()
                    && transaction["amount"] == money
                    && transaction["message"] == messageResponse) {
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

    static HttpEntity createTestImageMultipart() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
        File file = new File(getTestImageFilename())
        builder.addBinaryBody("file", new FileInputStream(file), ContentType.APPLICATION_OCTET_STREAM, file.getName())
        return builder.build()
    }

    static boolean checkDefaultImage(Object data) {
        return checkImage(data, getDefaultImageFilename())
    }

    static boolean checkTestImage(Object data) {
        return checkImage(data, getTestImageFilename())
    }

    private static boolean checkImage(Object data, String filename) {
        File file = new File(filename)
        if (data instanceof byte[]) {
            return data == file.getBytes()
        } else if (data instanceof ByteArrayInputStream) {
            return data.getBytes() == file.getBytes()
        }
        return false
    }

    static String getDefaultImagePath() {
        return "/api/v1/image/default"
    }

    static String getDefaultImageFilename() {
        return "src/main/resources/default.jpg"
    }

    static String getTestImageFilename() {
        return "src/test/resources/test.jpg"
    }
}