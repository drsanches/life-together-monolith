package ru.drsanches.life_together.utils

import net.sf.json.JSONArray
import net.sf.json.JSONObject
import ru.drsanches.life_together.data.debts.enumeration.TransactionType
import java.text.DateFormat
import java.text.SimpleDateFormat

class Utils {

    static JSONObject findTransaction(JSONArray history, String userId, TransactionType type, int money, String message) {
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

    static boolean checkTimestamp(Date timestampBefore, String timestamp, Date timeAfter) {
        String tmp = timestamp.replace("T", " ")
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX")
        Date date = df.parse(tmp)
        return date.after(timestampBefore) && date.before(timeAfter)
    }

    static Integer getAmount(JSONArray amounts, String userId) {
        for (JSONObject amount: (amounts as List<JSONObject>)) {
            if (amount["userId"] == userId) {
                return amount.getInt("amount")
            }
        }
        return null
    }
}