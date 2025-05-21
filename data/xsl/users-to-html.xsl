<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <head>
                <title>InterCom Users</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 20px;
                        background-color: #f0f2f5;
                    }
                    h1 {
                        color: #2c3e50;
                        text-align: center;
                    }
                    table {
                        width: 100%;
                        max-width: 800px;
                        margin: 20px auto;
                        border-collapse: collapse;
                        background-color: white;
                        box-shadow: 0 1px 3px rgba(0,0,0,0.2);
                    }
                    th, td {
                        padding: 12px;
                        text-align: left;
                        border-bottom: 1px solid #ddd;
                    }
                    th {
                        background-color: #3498db;
                        color: white;
                    }
                    tr:hover {
                        background-color: #f5f6f7;
                    }
                </style>
            </head>
            <body>
                <h1>InterCom Users</h1>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>IP Address</th>
                    </tr>
                    <xsl:for-each select="users/user">
                        <tr>
                            <td><xsl:value-of select="id"/></td>
                            <td><xsl:value-of select="username"/></td>
                            <td><xsl:value-of select="ip"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet> 