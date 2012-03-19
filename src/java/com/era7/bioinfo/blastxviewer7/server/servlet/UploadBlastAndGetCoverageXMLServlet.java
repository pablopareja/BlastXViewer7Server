/*
 * Copyright (C) 2010-2012  "Oh no sequences!"
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.era7.bioinfo.blastxviewer7.server.servlet;

import com.era7.bioinfo.blastxviewer7.server.RequestList;
import com.era7.bioinfo.blastxviewer7.server.util.FileUploadUtilities;
import com.era7.lib.bioinfo.bioinfoutil.blast.BlastExporter;
import com.era7.lib.bioinfoxml.BlastOutput;
import com.era7.lib.communication.xml.Request;
import com.era7.lib.communication.xml.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class UploadBlastAndGetCoverageXMLServlet extends HttpServlet {

    @Override
    public void init() {
    }

    @Override
    public void doPost(javax.servlet.http.HttpServletRequest request,
            javax.servlet.http.HttpServletResponse response)
            throws javax.servlet.ServletException, java.io.IOException {
        //System.out.println("doPost !");
        servletLogic(request, response);

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws javax.servlet.ServletException, java.io.IOException {
        //System.out.println("doGet !");
        servletLogic(request, response);


    }

    protected void servletLogic(HttpServletRequest request, HttpServletResponse resp)
            throws javax.servlet.ServletException, java.io.IOException {


        OutputStream out = resp.getOutputStream();

        String temp = request.getParameter(Request.TAG_NAME);

        try {

            Request myReq = new Request(temp);

            String method = myReq.getMethod();

            if (method.equals(RequestList.UPLOAD_BLAST_AND_GET_COVERAGE_XML_REQUEST)) {

                boolean isMultipart = ServletFileUpload.isMultipartContent(request);

                if (!isMultipart) {
                    Response response = new Response();
                    response.setError("No file was uploaded");
                    out.write(response.toString().getBytes());
                } else {

                    System.out.println("all controls passed!!");

                    FileItem fileItem = FileUploadUtilities.getFileItem(request);
                    InputStream uploadedStream = fileItem.getInputStream();
                    BufferedReader inBuff = new BufferedReader(new InputStreamReader(uploadedStream));
                    String line = null;
                    StringBuilder stBuilder = new StringBuilder();
                    while ((line = inBuff.readLine()) != null) {
                        //System.out.println("line = " + line);
                        stBuilder.append(line);
                    }

                    System.out.println("before blastExporter");

                    String resultExport = BlastExporter.exportBlastXMLtoIsotigsCoverage(new BlastOutput(stBuilder.toString()));

                    //System.out.println("resultExport = " + resultExport);

                    uploadedStream.close();

                    System.out.println("after blastexporter");

                    String responseSt = "<response status=\"" + Response.SUCCESSFUL_RESPONSE
                            + "\" method=\"" + RequestList.UPLOAD_BLAST_AND_GET_COVERAGE_XML_REQUEST
                            + "\" >\n" + resultExport + "\n</response>";

                    System.out.println("writing response");


                    resp.setContentType("text/html");

                    byte[] byteArray = responseSt.getBytes();
                    out.write(byteArray);
                    resp.setContentLength(byteArray.length);


                    System.out.println("doneee!!");

                }

            } else {
                Response response = new Response();
                response.setError("There is no such method");
                out.write(response.toString().getBytes());

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
