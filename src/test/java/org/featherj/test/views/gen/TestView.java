package org.featherj.test.views.gen;

import org.featherj.View;

public class TestView implements View {

    public String renderInherited() {
        return "";
    }

    public String render() {
        String newLine = System.getProperty("line.separator");
        StringBuilder view = new StringBuilder();
        view.append("<!DOCTYPE html>").append(newLine);
        view.append("<html>").append(newLine);
        view.append("<head>").append(newLine);
        view.append("    <title>Test Title</title>").append(newLine);
        view.append("    <script type=\"text/javascript\">").append(newLine);
        view.append("        function testFunc() {").append(newLine);
        view.append("            alert(\"testFunc alert\");").append(newLine);
        view.append("        }").append(newLine);
        view.append("    </script>").append(newLine);
        view.append("</head>").append(newLine);
        view.append("<body>").append(newLine);
        view.append("    <div class=\"container\">").append(newLine);
        view.append("        <div class=\"fluid-row\">").append(newLine);
        view.append("            ").append(newLine);

                        for (int i = 0; i < 12; i++) {

        view.append("").append(newLine);
        view.append("                    <div class=\"span1\"> ").append(newLine);
        view.append((  (i + 1) ) ).append(newLine);
        view.append(" </div>").append(newLine);
        view.append("                    ").append(newLine);

                        }

        view.append("").append(newLine);
        view.append("        </div>").append(newLine);
        view.append("    </div>").append(newLine);
        view.append("    <div>").append(newLine);
        view.append("        Escaped tag <\\%").append(newLine);
        view.append("        Escaped tag \\%>").append(newLine);
        view.append("    </div>").append(newLine);
        view.append("</body>").append(newLine);

        return view.toString();
    }
}