/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinax.docker.generator.utils;


import org.apache.commons.io.FileUtils;
import org.ballerinax.docker.generator.DockerGenConstants;
import org.ballerinax.docker.generator.exceptions.DockerGenException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Util methods used for artifact generation.
 */
public class DockerGenUtils {

    private static final boolean debugEnabled = "true".equals(System.getenv(DockerGenConstants.ENABLE_DEBUG_LOGS));
    private static final PrintStream out = System.out;

    /**
     * Prints a debug message.
     *
     * @param msg message to be printed
     */
    public static void printDebug(String msg) {
        if (debugEnabled) {
            out.println("debug [docker plugin]: " + msg);
        }
    }

    /**
     * Checks if a String is empty ("") or null.
     *
     * @param str the String to check, may be null
     * @return true if the String is empty or null
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }
    
    /**
     * Write content to a File. Create the required directories if they don't not exists.
     *
     * @param context        context of the file
     * @param targetFilePath target file path
     * @throws IOException If an error occurs when writing to a file
     */
    public static void writeToFile(String context, String targetFilePath) throws IOException {
        File newFile = new File(targetFilePath);
        if (newFile.exists() && newFile.delete()) {
            Files.write(Paths.get(targetFilePath), context.getBytes(StandardCharsets.UTF_8));
            return;
        }
        if (newFile.getParentFile().mkdirs()) {
            Files.write(Paths.get(targetFilePath), context.getBytes(StandardCharsets.UTF_8));
            return;
        }
        Files.write(Paths.get(targetFilePath), context.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Extract the ballerina file name from a given file path.
     *
     * @param uberJarFilePath balx file path.
     * @return output file name of balx
     */
    public static String extractUberJarName(String uberJarFilePath) {
        if (null != uberJarFilePath) {
            Path path = Paths.get(uberJarFilePath);
            if (null != path) {
                Path fileName = path.getFileName();
                if (null != fileName) {
                    String s = fileName.toString();
                    if (null != s) {
                        return s.replace("-executable", "").replace(".jar", "");
                    }
                }
            }
        }
    
        return null;
    }
    
    /**
     * Copy file or directory.
     *
     * @param source      source file/directory path
     * @param destination destination file/directory path
     */
    public static void copyFileOrDirectory(String source, String destination) throws DockerGenException {
        printDebug("copying file(s) from `" + source + "` to `" + destination + "`.");
        File src = new File(source);
        
        if (!src.exists()) {
            throw new DockerGenException("error while copying file/folder '" + source + "' as it does not exist");
        }
        
        File dst = new File(destination);
        try {
            
            // if source is file
            if (Files.isRegularFile(Paths.get(source))) {
                if (Files.isDirectory(dst.toPath())) {
                    // if destination is directory
                    FileUtils.copyFileToDirectory(src, dst);
                } else {
                    // if destination is file
                    FileUtils.copyFile(src, dst);
                }
            } else if (Files.isDirectory(Paths.get(source))) {
                FileUtils.copyDirectory(src, dst);
            }
        } catch (IOException e) {
            throw new DockerGenException("error while copying file/folder '" + source + "' to '" + destination + "'",
                    e);
        }
    }
    
    /**
     * Cleans error message getting rid of java class names.
     *
     * @param errorMessage The error message to be updated.
     * @return Cleaned error message.
     */
    public static String cleanErrorMessage(String errorMessage) {
        errorMessage = errorMessage.replace("javax.ws.rs.ProcessingException:", "");
        errorMessage = errorMessage.replace("java.io.IOException:", "");
        errorMessage = errorMessage.replace("java.util.concurrent.ExecutionException:", "");
        errorMessage = errorMessage.replace("java.lang.IllegalArgumentException:", "");
        errorMessage = errorMessage.replace("org.apache.http.conn.HttpHostConnectException:", "");
        errorMessage = errorMessage.replace("org.apache.http.client.ClientProtocolException:", "");
        
        if (errorMessage.contains("unable to find valid certification path")) {
            errorMessage = "unable to find docker cert path.";
        } else if (errorMessage.contains("Connection refused")) {
            errorMessage = "connection refused to docker host";
        } else if (errorMessage.contains("Unable to connect to server")) {
            errorMessage = errorMessage.replace("Unable to connect to server: Timeout: GET",
                    "unable to connect to docker host: ");
        } else if (errorMessage.toLowerCase(Locale.getDefault()).contains("permission denied")) {
            errorMessage = "permission denied for docker";
        }
    
        return firstCharToLowerCase(errorMessage);
    }
    
    /**
     * Lowercase first character of a {@link String}.
     *
     * @param str String variable.
     * @return Modified String.
     */
    private static String firstCharToLowerCase(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
    
        if (str.length() == 1) {
            return str.toLowerCase(Locale.getDefault());
        }
    
        char[] chArr = str.toCharArray();
        chArr[0] = Character.toLowerCase(chArr[0]);
    
        return new String(chArr);
    }
}
