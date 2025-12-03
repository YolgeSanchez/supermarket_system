package com.yolge;

import com.yolge.client.core.RestClient;
import com.yolge.client.exceptions.ApiException;

public class MainTest {
    public static void main(String[] args) {
        try {
            System.out.println("Intentando login...");
            RestClient.getInstance().login("admin", "admin123");
            System.out.println("Login exitoso! Token guardado.");

            RestClient.getInstance().login("", "");

        } catch (ApiException e) {
            System.err.println("ERROR API CAPTURADO: " + e.getMessage());
            System.err.println("Código: " + e.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}