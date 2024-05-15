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

import com.hcl.domino.DominoClient;
import com.hcl.domino.data.Document;
import com.hcl.domino.data.Item;
import com.hcl.domino.data.ItemDataType;
import com.hcl.domino.keep.info.richtext.impl.PlainRichtextProcessor;
import com.hcl.domino.mime.MimeData;
import io.vertx.core.json.JsonObject;
import org.jsoup.Jsoup;

import java.util.Optional;

public class PlainTextExtension extends PlainRichtextProcessor {

    @Override
    public JsonObject process(DominoClient dominoClient, Document document, String itemName) {
        Optional<ItemDataType> optItemType = document.getFirstItem(itemName)
                                                     .map(Item::getType);

        if (optItemType.isPresent() && optItemType.get() == ItemDataType.TYPE_MIME_PART) {
            // We only fix MIME part behaviour for now.
            MimeData mimeData = document.get(itemName, MimeData.class, null);
            if (null != mimeData) {
                String plainText = mimeData.getPlainText();

                if (null == plainText || plainText.isEmpty()) {
                    // If there is no plain text, we need to use HTML
                    plainText = Jsoup.parse(mimeData.getHtml())
                                     .text();
                }

                return JsonObject.of("type", "text/plain",
                        "encoding", "PLAIN",
                        "content", plainText
                );
            }
        }

        return super.process(dominoClient, document, itemName);
    }

    @Override
    public int getPriority() {
        return 5; // Higher number means higher priority
    }
}
