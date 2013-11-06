package com.huddle.handle.client.model;

public class MimeTypeExtensionMappings {

    private static final String fallback = "application/octet-stream";

    public static final String[][] types = new String [][] {

        {"doc","application/msword"},
        {"dot","application/msword"},
        {"pdf","application/pdf"},
        {"ai","application/postscript"},
        {"ps","application/postscript"},
        {"rtf","application/rtf"},
        {"xla","application/vnd.ms-excel"},
        {"xlc","application/vnd.ms-excel"},
        {"xlm","application/vnd.ms-excel"},
        {"xls","application/vnd.ms-excel"},
        {"xlt","application/vnd.ms-excel"},
        {"xlw","application/vnd.ms-excel"},
        {"pot","application/vnd.ms-powerpoint"},
        {"pps","application/vnd.ms-powerpoint"},
        {"ppt","application/vnd.ms-powerpoint"},
        {"wcm","application/vnd.ms-works"},
        {"wdb","application/vnd.ms-works"},
        {"wks","application/vnd.ms-works"},
        {"wps","application/vnd.ms-works"},
        {"z","application/x-compress"},
        {"tgz","application/x-compressed"},
        {"dvi","application/x-dvi"},
        {"gz","application/x-gzip"},
        {"latex","application/x-latex"},
        {"mdb","application/x-msaccess"},
        {"pub","application/x-mspublisher"},
        {"wri","application/x-mswrite"},
        {"swf","application/x-shockwave-flash"},
        {"tar","application/x-tar"},
        {"zip","application/zip"},
        {"mp3","audio/mpeg"},
        {"m3u","audio/x-mpegurl"},
        {"ra","audio/x-pn-realaudio"},
        {"ram","audio/x-pn-realaudio"},
        {"wav","audio/x-wav"},
        {"bmp","image/bmp"},
        {"gif","image/gif"},
        {"jpe","image/jpeg"},
        {"jpeg","image/jpeg"},
        {"jpg","image/jpeg"},
        {"jfif","image/pipeg"},
        {"svg","image/svg+xml"},
        {"tif","image/tiff"},
        {"tiff","image/tiff"},
        {"css","text/css"},
        {"htm","text/html"},
        {"html","text/html"},
        {"txt","text/plain"},
        {"rtx","text/richtext"},
        {"mp2","video/mpeg"},
        {"mpa","video/mpeg"},
        {"mpe","video/mpeg"},
        {"mpg","video/mpeg"},
        {"mpv2","video/mpeg"},
        {"mov","video/quicktime"},
        {"qt","video/quicktime"},
        {"asf","video/x-ms-asf"},
        {"3gp","video/3gpp"}
    };


    public static String getMimeTypeForExtension(String extension) {
        for (int i = 0; i < types.length; i++) {
            if (types[i][0].equals(extension)) {
                return types[i][1];
            }
        }
        return fallback;
    }
}
