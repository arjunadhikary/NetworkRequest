package com.arjun.learningone

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.util.*

class ParseXML {
    private val TAG = "ParseData"
    var appData = ArrayList<DataMap>()

    fun parseData(data: String): Boolean {
        var status = true
        var isEntry = false
        var textValue = ""
        try {
            Log.e(TAG, "parseData: Called")
            //setup xml parser
            val xmlParseXML = XmlPullParserFactory.newInstance()
            xmlParseXML.isNamespaceAware = true
            val xpp = xmlParseXML.newPullParser()
            //set Input For Parser to Parse
            xpp.setInput(data.reader())
            var eventType = xpp.eventType
            var currentRecord = DataMap()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xpp.name?.toLowerCase()
                when (eventType) {
                    XmlPullParser.START_TAG -> {
//                         Log.e(TAG, "parseData: $tagName")
                        isEntry = true
                    }
                    XmlPullParser.TEXT -> textValue = xpp.text
                    XmlPullParser.END_TAG -> {
                        if (isEntry) {
                            when (tagName) {
                                "entry" -> {
                                    appData.add(currentRecord)
                                    isEntry = false
                                    currentRecord = DataMap()
                                }
                                "title" -> currentRecord.title = textValue
                                "releasedate" -> currentRecord.releaseDate = textValue
                                "summary" -> currentRecord.link = textValue
                                "artist" -> currentRecord.artist = textValue
                            }
                        }
                    }

                }
                eventType = xpp.next()

            }

        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }

        return status

    }
}
