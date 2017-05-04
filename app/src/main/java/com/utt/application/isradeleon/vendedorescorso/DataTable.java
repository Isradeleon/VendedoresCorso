package com.utt.application.isradeleon.vendedorescorso;

/**
 * Created by Isra on 4/11/2017.
 */

public class DataTable {
    private int id;
    private String name;
    private String email;
    private String token;
    private int id_token;

    public DataTable(){}
    public DataTable(int id, String name, String email, String token, int id_token) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.token = token;
        this.id_token = id_token;
    }

    //Getters and Setters

    //Table data
    public static final String TABLE_NAME="data_table";

    public static final String C_ID="id";
    public static final String C_NAME ="name";
    public static final String C_EMAIL ="email";
    public static final String C_TOKEN ="token";
    public static final String C_ID_TOKEN ="id_token";

    public static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+"("+
            C_ID+" INTEGER PRIMARY KEY,"+
            C_NAME +" TEXT NOT NULL,"+
            C_EMAIL +" TEXT NOT NULL,"+
            C_TOKEN +" TEXT NOT NULL,"+
            C_ID_TOKEN +" INTEGER NOT NULL"+
            ");";
}
