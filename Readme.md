
# Domino REST API (Keep) Extension for Plain Text

## Overview

This project serves as a sample and boilerplate for extending the capabilities of the Domino REST API. It utilizes the [RichTextExtension extensibility](https://opensource.hcltechsw.com/Domino-rest-api/references/richtextension.html) available from version 1.0.12. This project provides developers with a starting point for enhancing the functionality of the Domino REST API through custom extensions.

The extension also addresses and resolves the issue where the Domino REST API previously returned an empty response when requesting plain text from a MIME field containing only HTML data. By leveraging the [JSoup library](https://jsoup.org), it converts HTML content to plain text.

## Using the Extension

To use the extension, simply place the `keep-ext-plaintext-x.y.jar` file into the `<RESTAPI-INSTALL>/libs` directory and restart the `restapi` task.

## Compiling the Project

Extension projects depend on the Keep-Core library during compilation. Therefore, you need to import the core library to your local Maven repository to compile the project.

You can either follow the documented [steps](https://opensource.hcltechsw.com/Domino-rest-api/references/richtextension.html) to create your own Helper project, or simply run the [script](https://github.com/sbasegmez/KeepExt-PlainText/blob/main/scripts/install_keep_jar_to_maven.sh) we provided to install the Keep-Core JAR file (and its Javadoc file) to your local Maven repository. 

In every version, Keep provides a new version of Keep-Core library. The script automatically determine the latest version in the given folder. Also, remember that versioning is different for library files and the product (Domino Rest API 1.0.12 uses `keep-core-1.30.6.jar`).

## Miscellaneous

- If you are using macOS with System Integrity Protection (introduced in El Capitan) enabled, you cannot run tests over Maven because SIP strips the `DYLD_LIBRARY_PATH` environment variable from the test process. Use your IDE's own testing capabilities instead and skip tests in Maven. 

