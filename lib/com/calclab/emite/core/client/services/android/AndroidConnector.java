/*
 *
 * ((e)) emite: A pure gwt (Google Web Toolkit) xmpp (jabber) library
 *
 * (c) 2008-2009 The emite development team (see CREDITS for details)
 * This file is part of emite.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.calclab.emite.core.client.services.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;

import com.calclab.emite.core.client.services.ConnectorCallback;

public class AndroidConnector {
    public static void send(final String httpBase, final String request, final ConnectorCallback listener) {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    HttpPost method = new HttpPost(httpBase);
                    method.addHeader("Content-Type", "text/xml; charset=\"utf-8\"");
                    method.setEntity(new StringEntity(request, "utf-8"));

                    final HttpResponse response = new DefaultHttpClient().execute(method);
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                HttpEntity entity  = response.getEntity();
                                String     content = readInputStream(entity.getContent());
                                listener.onResponseReceived(response.getStatusLine().getStatusCode(), content);
                            } catch (Throwable e) {
                                listener.onError(request, e);
                            }
                        }
                    });
                } catch (final Throwable e) {
                    handler.post(new Runnable() {
                        public void run() {
                            listener.onError(request, e);
                        }
                    });
                }
            }
        };
        new Thread(r).start();
    }

    private static String readInputStream(InputStream stream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuilder  sb = new StringBuilder();

            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            br.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final Handler handler = new Handler();
}
