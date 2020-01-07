/*
The MIT License (MIT)
[OSI Approved License]
The MIT License (MIT)

Copyright (c) 2014 Daniel Glasson

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package com.example.gestor_tab.geocode;

import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.ClientsManager;
import com.example.gestor_tab.geocode.kdtree.KDTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * Created by Daniel Glasson on 18/05/2014.
 * Uses KD-trees to quickly find the nearest point
 * 
 * ReverseGeoCode reverseGeoCode = new ReverseGeoCode(new FileInputStream("c:\\AU.txt"), true);
 * System.out.println("Nearest to -23.456, 123.456 is " + geocode.nearestPlace(-23.456, 123.456));
 */
public class ReverseGeoCodeCliente {
    KDTree<GeoCliente> kdTree;

    public ReverseGeoCodeCliente(ArrayList<Cliente> clientes) {
        try {
            createKdTree(clientes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createKdTree(ArrayList<Cliente> clientes)
            throws IOException {
        ArrayList<GeoCliente> arPlaceNames;
        arPlaceNames = new ArrayList<GeoCliente>();

        for (Cliente cliente : clientes) {
            if (cliente.getLat() != 0)
                arPlaceNames.add(new GeoCliente(cliente.getId(), cliente.getLat(), cliente.getLng()));
        }
        kdTree = new KDTree<GeoCliente>(arPlaceNames);
    }

    public GeoCliente nearestPlace(double latitude, double longitude) {
        return kdTree.findNearest(new GeoCliente(-1, latitude,longitude));
    }
}
