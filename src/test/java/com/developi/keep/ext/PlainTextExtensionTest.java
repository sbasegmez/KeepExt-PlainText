/**
 * Copyright (c) 2024 Serdar Basegmez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.developi.keep.ext;

import com.developi.domino.test.AbstractNotesRuntimeTest;
import com.hcl.domino.DominoClient;
import com.hcl.domino.commons.org.apache.commons.mail.Email;
import com.hcl.domino.commons.org.apache.commons.mail.HtmlEmail;
import com.hcl.domino.commons.org.apache.commons.mail.SimpleEmail;
import com.hcl.domino.commons.util.StringUtil;
import com.hcl.domino.data.Document;
import com.hcl.domino.mime.MimeWriter;
import io.vertx.core.json.JsonObject;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@Disabled("Not ready")
class PlainTextExtensionTest extends AbstractNotesRuntimeTest {

    private final static String TEXT_CONTENT = "Lorem Ipsum dolor sit amet";
    private final static String HTML_CONTENT = "Lorem\n<div>Ipsum <br>\n<b> dolor</b></div>";
    private final static String HTML_CONTENT_EXPECTED = "Lorem Ipsum dolor";


    @Test
    void test_MimeWithTextOnly() throws Exception {
        final PlainTextExtension extension = new PlainTextExtension();

        this.withTempDb(database -> {
            DominoClient client = getClient();
            Document document = database.createDocument();

            createMime(document, "Body", TEXT_CONTENT, null);

            JsonObject result = extension.process(getClient(), document, "Body");

            // type and encoding can be tested once.
            assertEquals(result.getString("type"), "text/plain");
            assertEquals(result.getString("encoding"), "PLAIN");

            assertEquals(result.getString("content"), TEXT_CONTENT);
        });
    }

    @Test
    void test_MimeWithHtmlOnly() throws Exception {
        final PlainTextExtension extension = new PlainTextExtension();

        this.withTempDb(database -> {
            DominoClient client = getClient();
            Document document = database.createDocument();

            createMime(document, "Body", null, HTML_CONTENT);

            JsonObject result = extension.process(getClient(), document, "Body");

            assertEquals(result.getString("content"), HTML_CONTENT_EXPECTED);
        });
    }

    @Test
    void test_MimeWithTextAndHtml() throws Exception {
        final PlainTextExtension extension = new PlainTextExtension();

        this.withTempDb(database -> {
            DominoClient client = getClient();
            Document document = database.createDocument();

            createMime(document, "Body", TEXT_CONTENT, HTML_CONTENT);

            JsonObject result = extension.process(getClient(), document, "Body");

            assertEquals(result.getString("content"), TEXT_CONTENT);
        });
    }

    private void createMime(Document doc, String fieldName, String textPlain, String textHtml) throws Exception {
        // Create MimeWriter
        MimeWriter mw = doc.getParentDatabase()
                           .getParentDominoClient()
                           .getMimeWriter();

        Email email;
        MimeMessage mimeMessage;

        boolean text = StringUtil.isNotEmpty(textPlain);
        boolean html = StringUtil.isNotEmpty(textHtml);

        if (text && html) {
            // Multipart
            email = new HtmlEmail();
            ((HtmlEmail) email).setTextMsg(textPlain);
            ((HtmlEmail) email).setHtmlMsg(textHtml);
        } else {
            email = new SimpleEmail();
            if (text) {
                email.setContent(textPlain, "text/plain; charset=UTF-8");
            } else {
                email.setContent(textHtml, "text/html; charset=UTF-8");
            }
        }

        email.setHostName("localhost");
        email.setFrom("test@example.com");
        email.setTo(Arrays.asList(InternetAddress.parse("test@example.com")));

        email.buildMimeMessage();

        mimeMessage = email.getMimeMessage();

        mw.writeMime(
                doc,
                fieldName,
                mimeMessage,
                EnumSet.of(MimeWriter.WriteMimeDataType.BODY, MimeWriter.WriteMimeDataType.HEADERS)
        );
    }


}