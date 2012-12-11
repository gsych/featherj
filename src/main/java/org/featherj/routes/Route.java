package org.featherj.routes;

import org.featherj.Request;
import org.featherj.actions.ActionResult;
import org.featherj.routes.params.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public abstract class Route {

    private abstract class RoutePart {
        private final String str;

        public RoutePart(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public abstract int match(String url, int currentUrlIndex);
    }

    private class SlashRoutePart extends RoutePart {

        public SlashRoutePart() {
            super("/");
        }

        @Override
        public int match(String url, int currentUrlIndex) {
            if (url.charAt(currentUrlIndex) == '/') {
                currentUrlIndex++;
                return currentUrlIndex;
            }

            return -1;
        }
    }

    private class UrlRoutePart extends RoutePart {
        public UrlRoutePart(String str) {
            super(str);
        }

        @Override
        public int match(String url, int currentUrlIndex) {
            String str = getStr();
            if (str.regionMatches(0, url, currentUrlIndex, str.length())) {
                return currentUrlIndex + str.length();
            }

            return -1;
        }
    }

    private class ParamKeyRoutePart extends RoutePart {
        private Param<?> param;

        public ParamKeyRoutePart(Param<?> param) {
            super(param.getKey());
            this.param = param;
        }

        @Override
        public int match(String url, int currentUrlIndex) {
            Matcher m = param.getExpr().matcher(url);
            if (m.find(currentUrlIndex)) {
                param.extractValue(m.group());
                return m.end();
            }

            return -1;
        }
    }

    private class UrlPatternParser {
        private final Map<String, Param<?>> params;
        private String urlPattern;
        private int i;
        private StringBuilder currentPartStr;
        private ArrayList<RoutePart> readyParts;

        public UrlPatternParser(Map<String, Param<?>> params) {
            this.params = params;
        }

        public RoutePart[] parse(String urlPattern) throws UrlParseException {
            this.urlPattern = urlPattern;
            i = 0;
            currentPartStr = new StringBuilder();
            readyParts = new ArrayList<RoutePart>();

            while (hasNext()) {
                char ch = peek();
                if (ch == '/') {
                    slashPart();
                }
                else if (ch == ':') {
                    paramPart();
                }
                else {
                    urlPart();
                }
            }

            return readyParts.toArray(new RoutePart[readyParts.size()]);
        }

        private char read() {
            char ch = urlPattern.charAt(i);
            currentPartStr.append(ch);
            i++;
            return ch;
        }

        private void read(char match) throws UrlParseException {
            char ch = peek();
            if (ch != match) {
                throw new UrlParseException("'" + match + "' is expected, but got '" + ch + "'.");
            }
            read();
        }

        private char peek() {
            if (!hasNext()) {
                return 0;
            }
            return urlPattern.charAt(i);
        }

        private boolean hasNext() {
            return i < urlPattern.length();
        }

        private void slashPart() throws UrlParseException {
            read('/');
            readyParts.add(new SlashRoutePart());
            currentPartStr = new StringBuilder();
        }

        private void paramPart() throws UrlParseException {
            read(':');
            int index = i;
            while (hasNext() && Character.isLetterOrDigit(peek())) {
                read();
            }

            if (index == i) {
                throw new UrlParseException("A parse parameter identifier expected (e.g. \":my_id\") after " + getParsedPrefix());
            }

            String paramKey = currentPartStr.toString();
            readyParts.add(new ParamKeyRoutePart(resolveParam(paramKey)));
            currentPartStr = new StringBuilder();
        }

        private Param<?> resolveParam(String paramKey) throws UrlParseException {
            Param<?> param = this.params.get(paramKey);
            if (param == null) {
                throw new UrlParseException(
                    "URL pattern parameter reference \"" + paramKey + "\" doesn't have corresponding Param declaration.");
            }
            return param;
        }

        private void urlPart() throws UrlParseException {
            char ch = peek();
            int index = i;
            while (hasNext() && ch != '/' && ch != ':') {
                read();
                ch = peek();
            }
            if (index == i) {
                throw new UrlParseException("An URL part is expected after " + getParsedPrefix());
            }

            readyParts.add(new UrlRoutePart(currentPartStr.toString()));
            currentPartStr = new StringBuilder();
        }

        private String getParsedPrefix() {
            return "\"" + urlPattern.substring(0, i) + "\"";
        }
    }

    private final RoutePart[] urlPatternParts;

    public Route(String urlPattern, Param<?>...params) throws UrlParseException {
        Map<String, Param<?>> paramsMap = new HashMap<String, Param<?>>();
        for (Param<?> p : params) {
            paramsMap.put(p.getKey(), p);
            p.clearValue();
        }

        this.urlPatternParts = new UrlPatternParser(paramsMap).parse(urlPattern);
    }

    public boolean matches(Request request) {
        String requestUrl = request.getCompleteUrl();
        int requestUrlIndex = 0;

        for (RoutePart part : urlPatternParts) {
            requestUrlIndex = part.match(requestUrl, requestUrlIndex);
            if (requestUrlIndex == -1) {
                return false;
            }
        }

        return requestUrl.length() == requestUrlIndex;

    }

    public abstract ActionResult runAction(Request request) throws Exception;
}
