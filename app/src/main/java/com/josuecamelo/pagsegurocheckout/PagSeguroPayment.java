package com.josuecamelo.pagsegurocheckout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Use this class to pay over pagseguro<br/>
 * <br/>Author: Josué Camelo, 23/11/2021
 */
public class PagSeguroPayment {

    private Activity activity;
    private ProgressDialog progressDialog;

    public static final String PAG_SEGURO_EXTRA = "PAG_SEGURO_EXTRA";
    public static final int PAG_SEGURO_REQUEST_CODE = 111;
    public static final int PAG_SEGURO_REQUEST_SUCCESS_CODE = 222;
    public static final int PAG_SEGURO_REQUEST_FAILURE_CODE = 333;
    public static final int PAG_SEGURO_REQUEST_CANCELLED_CODE = 444;


    public PagSeguroPayment(Activity activity) {
        this.activity = activity;
        this.progressDialog =  new ProgressDialog(activity);
    }

    public void pay(String checkoutXml) {
        progressDialog.setTitle(this.activity.getString(R.string.pagseguro));
        progressDialog.setMessage(this.activity.getString(R.string.waiting_for_answer));
        progressDialog.show();

        StringEntity checkoutEntity = null;

        final String vendorEmail = activity.getString(R.string.pagseguro_vendor_email);
        final String vendorToken = activity.getString(R.string.pagseguro_vendor_token);
        final String webService = activity.getString(R.string.pagseguro_webservice_checkout_address);
        final String pagseguroWsRequestAddress = String.format(webService, vendorEmail, vendorToken);

        //nova implementação
        checkoutXml = "<?xml version=\"1.0\"?>\n" +
                "<checkout>\n" +
                "  <sender>\n" +
                "    <name>Jose Comprador</name>\n" +
                "    <email>comprador@uol.com.br</email>\n" +
                "    <phone>\n" +
                "      <areaCode>99</areaCode>\n" +
                "      <number>999999999</number>\n" +
                "    </phone>\n" +
                "    <documents>\n" +
                "      <document>\n" +
                "        <type>CPF</type>\n" +
                "        <value>11475714734</value>\n" +
                "      </document>\n" +
                "    </documents>\n" +
                "  </sender>\n" +
                "  <currency>BRL</currency>\n" +
                "  <items>\n" +
                "    <item>\n" +
                "      <id>0001</id>\n" +
                "      <description>Produto PagSeguroI</description>\n" +
                "      <amount>99999.99</amount>\n" +
                "      <quantity>1</quantity>\n" +
                "      <weight>10</weight>\n" +
                "      <shippingCost>1.00</shippingCost>\n" +
                "    </item>\n" +
                "  </items>\n" +
                "  <redirectURL>http://lojamodelo.com.br/return.html</redirectURL>\n" +
                "  <extraAmount>10.00</extraAmount>\n" +
                "  <reference>REF1234</reference>\n" +
                "  <shipping>\n" +
                "    <address>\n" +
                "      <street>Av. PagSeguro</street>\n" +
                "      <number>9999</number>\n" +
                "      <complement>99o andar</complement>\n" +
                "      <district>Jardim Internet</district>\n" +
                "      <city>Cidade Exemplo</city>\n" +
                "      <state>SP</state>\n" +
                "      <country>BRA</country>\n" +
                "      <postalCode>99999999</postalCode>\n" +
                "    </address>\n" +
                "    <type>1</type>\n" +
                "    <cost>1.00</cost>\n" +
                "    <addressRequired>true</addressRequired>\n" +
                "  </shipping>\n" +
                "  <timeout>25</timeout>\n" +
                "  <maxAge>999999999</maxAge>\n" +
                "  <maxUses>999</maxUses>\n" +
                "  <receiver>\n" +
                "    <email>suporte@lojamodelo.com.br</email>\n" +
                "  </receiver>\n" +
                "  <enableRecover>false</enableRecover>\n" +
                "</checkout>";

        try {
            checkoutEntity = new StringEntity(checkoutXml);
            // very important step, if you don`t set this, the request will fail
            checkoutEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/xml"));
            checkoutEntity.setContentEncoding("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            Log.d("PAG_SEGURO", e.getMessage());
        }
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(activity, pagseguroWsRequestAddress, checkoutEntity, "application/xml", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String response = null;
                try {
                    response = new String(responseBody, "ISO-8859-1");
                } catch (UnsupportedEncodingException e) {
                    Log.d("PAG_SEGURO", e.getMessage());
                }
                try {
                    // read checkout code
                    final XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(new StringReader(response));
                    int eventType = parser.getEventType();
                    String checkoutCode="";
                    String checkoutDate="";
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if(eventType == XmlPullParser.START_TAG) {
                            if(parser.getName().equalsIgnoreCase("code")){
                                parser.next();
                                checkoutCode = parser.getText();
                                parser.next();
                            }
                        }
                        if(eventType == XmlPullParser.START_TAG) {
                            if(parser.getName().equalsIgnoreCase("checkoutDate")){
                                parser.next();
                                checkoutDate = parser.getText();
                                parser.next();
                            }
                        }
                        eventType = parser.next();
                    }
                    final String paymentAddress = activity.getString(R.string.pagseguro_payment_page);
                    final String paymentPage = String.format(paymentAddress, checkoutCode);

                    final Intent pagseguro = new Intent(activity, PagSeguroActivity.class);
                    pagseguro.putExtra("uri", paymentPage);
                    activity.startActivityForResult(pagseguro, PAG_SEGURO_REQUEST_CODE);
                    progressDialog.hide();

                }catch (XmlPullParserException e) {
                    Log.d("PAG_SEGURO", e.getMessage());
                } catch (IOException e) {
                    Log.d("PAG_SEGURO", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                StringBuilder errors = new StringBuilder();
                errors.append("List of errors\n");
                errors.append("Statuscode: " + statusCode + "\n");
                String response = null;
                try {
                    response = new String(responseBody, "ISO-8859-1");
                } catch (UnsupportedEncodingException e) {
                    Log.d("PAG_SEGURO", e.getMessage());
                }
                try {
                    final XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(new StringReader(response));
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            if (parser.getName().equalsIgnoreCase("error")) {
                                parser.next();
                                errors.append(parser.getText() + "\n");
                                parser.next();
                            }
                        }
                        eventType = parser.next();
                    }
                    Toast.makeText(activity, errors.toString(), Toast.LENGTH_LONG).show();

                } catch (XmlPullParserException e) {
                    Log.d("PAG_SEGURO", e.getMessage());
                } catch (IOException e) {
                    Log.d("PAG_SEGURO", e.getMessage());
                }
            }
        });
    }
}
