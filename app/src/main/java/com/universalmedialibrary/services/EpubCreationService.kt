package com.universalmedialibrary.services

import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpubCreationService @Inject constructor() {

    fun createEpubFile(title: String, author: String, htmlContent: String, filePath: String) {
        ZipOutputStream(FileOutputStream(filePath)).use { zos ->
            // Add mimetype file (must be first and uncompressed)
            val mimeEntry = ZipEntry("mimetype")
            mimeEntry.method = ZipEntry.STORED
            mimeEntry.size = "application/epub+zip".toByteArray().size.toLong()
            mimeEntry.crc = 0x2CAB616F // CRC-32 for "application/epub+zip"
            zos.putNextEntry(mimeEntry)
            zos.write("application/epub+zip".toByteArray())
            zos.closeEntry()

            // Add container.xml
            zos.putNextEntry(ZipEntry("META-INF/container.xml"))
            zos.write(getContainerXml().toByteArray())
            zos.closeEntry()

            // Add content.opf
            zos.putNextEntry(ZipEntry("OEBPS/content.opf"))
            zos.write(getContentOpf(title, author).toByteArray())
            zos.closeEntry()

            // Add toc.ncx
            zos.putNextEntry(ZipEntry("OEBPS/toc.ncx"))
            zos.write(getTocNcx(title).toByteArray())
            zos.closeEntry()

            // Add chapter content
            zos.putNextEntry(ZipEntry("OEBPS/chapter1.xhtml"))
            zos.write(getChapterXhtml(title, htmlContent).toByteArray())
            zos.closeEntry()
        }
    }

    private fun getContainerXml(): String = """
        <?xml version="1.0"?>
        <container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
          <rootfiles>
            <rootfile full-path="OEBPS/content.opf" media-type="application/oebps-package+xml"/>
          </rootfiles>
        </container>
    """.trimIndent()

    private fun getContentOpf(title: String, author: String): String = """
        <?xml version="1.0"?>
        <package version="2.0" xmlns="http://www.idpf.org/2007/opf" unique-identifier="bookid">
          <metadata xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:opf="http://www.idpf.org/2007/opf">
            <dc:title>$title</dc:title>
            <dc:creator opf:role="aut">$author</dc:creator>
            <dc:language>en</dc:language>
            <meta name="cover" content="cover-image" />
          </metadata>
          <manifest>
            <item id="ncx" href="toc.ncx" media-type="application/x-dtbncx+xml"/>
            <item id="chapter1" href="chapter1.xhtml" media-type="application/xhtml+xml"/>
          </manifest>
          <spine toc="ncx">
            <itemref idref="chapter1"/>
          </spine>
        </package>
    """.trimIndent()

    private fun getTocNcx(title: String): String = """
        <?xml version="1.0"?>
        <!DOCTYPE ncx PUBLIC "-//NISO//DTD ncx 2005-1//EN" "http://www.daisy.org/z3986/2005/ncx-2005-1.dtd">
        <ncx version="2005-1" xmlns="http://www.daisy.org/z3986/2005/ncx/">
          <head>
            <meta name="dtb:uid" content="some-unique-id"/>
            <meta name="dtb:depth" content="1"/>
            <meta name="dtb:totalPageCount" content="0"/>
            <meta name="dtb:maxPageNumber" content="0"/>
          </head>
          <docTitle><text>$title</text></docTitle>
          <navMap>
            <navPoint id="navpoint-1" playOrder="1">
              <navLabel><text>Chapter 1</text></navLabel>
              <content src="chapter1.xhtml"/>
            </navPoint>
          </navMap>
        </ncx>
    """.trimIndent()

    private fun getChapterXhtml(title: String, content: String): String = """
        <?xml version="1.0" encoding="utf-8"?>
        <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
        <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <title>$title</title>
        </head>
        <body>
          <h1>$title</h1>
          $content
        </body>
        </html>
    """.trimIndent()
}
