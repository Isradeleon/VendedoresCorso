package com.utt.application.isradeleon.vendedorescorso;

import org.json.JSONArray;

/**
 * Created by Isra on 4/12/2017.
 */
public class SingleJSON {
    private JSONArray jsonArray;

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    private static SingleJSON ourInstance = new SingleJSON();

    public static SingleJSON getInstance() {
        return ourInstance;
    }

    private SingleJSON() {
    }
}
