package com.tydic.konw.test;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Created by pfm on 2018/8/2.
 */
/**
 * Examples of using different Content Handlers to
 * get different parts of the file's contents
 */
public class ContentHandlerExample {
    /**
     * Example of extracting the plain text of the contents.
     * Will return only the "body" part of the document
     */
    public String parseToPlainText() throws IOException, SAXException, TikaException {
        BodyContentHandler handler = new BodyContentHandler(10000000);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (InputStream stream = ContentHandlerExample.class.getResourceAsStream("/TikaSrc/[2012][288 Pages]AMD APP OpenCL Programming Guide Revision 2.4.pdf")) {
            parser.parse(stream, handler, metadata);
            return handler.toString();
        }

//
//        Tika tika = new Tika();
//            return tika.parseToString(new File("F:\\work\\idea_proj2\\SourceIndexing\\src\\main\\resources\\TikaSrc\\省份编码.docx"));

    }

    /**
     * Example of extracting the contents as HTML, as a string.
     */
    public String parseToHTML() throws IOException, SAXException, TikaException {
        ContentHandler handler = new ToXMLContentHandler();

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (InputStream stream = ContentHandlerExample.class.getResourceAsStream("/TikaSrc/省份编码.docx")) {
            parser.parse(stream, handler, metadata);
            return handler.toString();
        }
    }

    /**
     * Example of extracting just the body as HTML, without the
     * head part, as a string
     */
    public String parseBodyToHTML() throws IOException, SAXException, TikaException {
        ContentHandler handler = new BodyContentHandler(
                new ToXMLContentHandler());

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (InputStream stream = ContentHandlerExample.class.getResourceAsStream("test.doc")) {
            parser.parse(stream, handler, metadata);
            return handler.toString();
        }
    }

    /**
     * Example of extracting just one part of the document's body,
     * as HTML as a string, excluding the rest
     */
    public String parseOnePartToHTML() throws IOException, SAXException, TikaException {
        // Only get things under html -> body -> div (class=header)
        XPathParser xhtmlParser = new XPathParser("xhtml", XHTMLContentHandler.XHTML);
        Matcher divContentMatcher = xhtmlParser.parse("/xhtml:html/xhtml:body/xhtml:div/descendant::node()");
        ContentHandler handler = new MatchingContentHandler(
                new ToXMLContentHandler(), divContentMatcher);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (InputStream stream = ContentHandlerExample.class.getResourceAsStream("test2.doc")) {
            parser.parse(stream, handler, metadata);
            return handler.toString();
        }
    }

    protected final int MAXIMUM_TEXT_CHUNK_SIZE = 40;

    /**
     * Example of extracting the plain text in chunks, with each chunk
     * of no more than a certain maximum size
     */
    public List<String> parseToPlainTextChunks() throws IOException, SAXException, TikaException {
        final List<String> chunks = new ArrayList<>();
        chunks.add("");
        ContentHandlerDecorator handler = new ContentHandlerDecorator() {
            @Override
            public void characters(char[] ch, int start, int length) {
                String lastChunk = chunks.get(chunks.size() - 1);
                String thisStr = new String(ch, start, length);

                if (lastChunk.length() + length > MAXIMUM_TEXT_CHUNK_SIZE) {
                    chunks.add(thisStr);
                } else {
                    chunks.set(chunks.size() - 1, lastChunk + thisStr);
                }
            }
        };

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (InputStream stream = ContentHandlerExample.class.getResourceAsStream("test2.doc")) {
            parser.parse(stream, handler, metadata);
            return chunks;
        }
    }
}