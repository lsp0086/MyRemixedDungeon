package com.nyrds.platform.app

import com.nyrds.platform.game.RemixedDungeon
import com.nyrds.util.ModdingMode
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import fi.iki.elonen.NanoHTTPD
import java.io.File

class WebServer(port: Int) : NanoHTTPD(port) {
    private fun defaultHead(): String {
        return "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head>"
    }

    private fun serveRoot(): String {
        var msg = "<html><body>"
        msg += defaultHead()
        msg += Utils.format(
            "<br>RemixedDungeon: %s (%d)",
            RemixedDungeon.version,
            RemixedDungeon.versionCode
        )
        msg += Utils.format(
            "<br>Mod: %s (%d)",
            ModdingMode.activeMod(),
            ModdingMode.activeModVersion()
        )
        if (Dungeon.level != null) {
            msg += Utils.format("<br>Level: %s", Dungeon.level.levelId)
        }
        msg += "</p></body><html>"
        return msg
    }

    private fun serveList(): String {
        val msg = StringBuilder("<html><body>")
        msg.append(defaultHead())
        listDir(msg, "")
        msg.append("</p></body><html>")

        return msg.toString()
    }

    private fun serveFs(file: String): Response {
        if (ModdingMode.isResourceExist(file)) {
            val fis = ModdingMode.getInputStream(file)
            val response = newChunkedResponse(Response.Status.OK, "application/octet-stream", fis)
            response.addHeader("Content-Disposition", "attachment; filename=\"$file\"")
            return response
        } else {
            val msg = StringBuilder("<html><body>")
            msg.append(defaultHead())
            val upOneLevel =
                if (file.contains("/")) file.substring(0, file.lastIndexOf("/")) else ""
            msg.append(Utils.format("<br><a href=\"/fs/%s\">%s</a>", upOneLevel, ".."))
            if (file.isNotEmpty()) {
                listDir(msg, "$file/")
            } else {
                listDir(msg, "")
            }
            msg.append("</p></body><html>")
            return newFixedLengthResponse(Response.Status.OK, "text/html", msg.toString())
        }
    }


    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        GLog.debug("WebServer: $uri")

        if (session.method == Method.GET) {
            if (uri == "/") {
                return newFixedLengthResponse(Response.Status.OK, "text/html", serveRoot())
            }

            if (uri == "/list") {
                return newFixedLengthResponse(Response.Status.OK, "text/html", serveList())
            }

            if (uri.startsWith("/fs/")) {
                return serveFs(uri.substring(4))
            }
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html", "Not Found")
    }


    companion object {
        private fun listDir(msg: StringBuilder, path: String) {
            val list = ModdingMode.listResources(
                path
            ) { _: File?, _: String? -> true }
            list.sort()

            for (name in list) {
                if (path.isEmpty()) {
                    msg.append(Utils.format("<br><a href=\"/fs/%s\">%s</a>", name, name))
                } else {
                    msg.append(
                        Utils.format(
                            "<br><a href=\"/fs/%s%s\">%s%s</a>",
                            path,
                            name,
                            path,
                            name
                        )
                    )
                }
            }
        }
    }
}