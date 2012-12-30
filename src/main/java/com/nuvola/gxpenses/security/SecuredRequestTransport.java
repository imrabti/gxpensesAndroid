package com.nuvola.gxpenses.security;

import android.util.Base64;
import com.google.web.bindery.requestfactory.shared.RequestTransport;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.nuvola.gxpenses.ioc.ApiURL;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

public class SecuredRequestTransport implements RequestTransport {
    private final SecurityUtils securityUtils;
    private final URI uri;

    @Inject
    public SecuredRequestTransport(SecurityUtils securityUtils, @ApiURL String apiUrl) throws URISyntaxException {
        this.securityUtils = securityUtils;
        this.uri = new URI(apiUrl);
    }

    public void send(String payload, TransportReceiver receiver) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost();
        post.setHeader("Content-Type", "application/json;charset=UTF-8");

        if (securityUtils.isLoggedIn()) {
            String username = securityUtils.getUsername();
            String password = securityUtils.getPassword();
            post.addHeader("Authorization", "Basic " + Base64.encodeToString((username + ":" + password).getBytes(),
                    Base64.NO_WRAP));
        }

        post.setURI(uri);
        Throwable ex;
        try {
            post.setEntity(new StringEntity(payload, "UTF-8"));
            HttpResponse response = client.execute(post);
            if (200 == response.getStatusLine().getStatusCode()) {
                String contents = readStreamAsString(response.getEntity().getContent());
                receiver.onTransportSuccess(contents);
            } else {
                receiver.onTransportFailure(new ServerFailure(response.getStatusLine().getReasonPhrase()));
            }
            return;
        } catch (UnsupportedEncodingException e) {
            ex = e;
        } catch (ClientProtocolException e) {
            ex = e;
        } catch (IOException e) {
            ex = e;
        }

        receiver.onTransportFailure(new ServerFailure(ex.getMessage()));
    }

    private String readStreamAsString(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int count;
            do {
                count = in.read(buffer);
                if (count > 0) {
                    out.write(buffer, 0, count);
                }
            } while (count >= 0);
            return out.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("The JVM does not support the compiler's default encoding.", e);
        } catch (IOException e) {
            return null;
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }
}
