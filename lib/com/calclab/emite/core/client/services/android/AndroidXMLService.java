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

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.packet.android.AndroidPacket;

public class AndroidXMLService {
    public static String toString(final IPacket packet) {
	return packet.toString();
    }

    public static IPacket toXML(final String xml) {
	Document document = parse(xml);
        Element  element  = null;
        if (document != null) {
            element = document.getDocumentElement();
        }

        if (element != null) {
            return new AndroidPacket(element);
        } else {
            return null;
        }
    }

    private static Document parse(String xml) {
        StringReader sr = new StringReader("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" + xml);
        InputSource  in = new InputSource(sr);
        try {
			return factory.newDocumentBuilder().parse(in);
		} catch (Exception e) {
			return null;
		}
    }

    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    {
        factory.setNamespaceAware(false);
    }
}
