/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web.directive.base;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;

import java.util.ArrayList;
import java.util.List;


public abstract class PaginateDirectiveBase extends JbootDirectiveBase {


    private String previousClass = "previous";
    private String nextClass = "next";
    private String activeClass = "active";
    private String disabledClass = "disabled";
    private String anchor = "anchor";
    private boolean onlyShowPreviousAndNext = false;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        previousClass = getPara("previousClass", scope, "previous");
        nextClass = getPara("nextClass", scope, "next");
        activeClass = getPara("activeClass", scope, "active");
        disabledClass = getPara("disabledClass", scope, "disabled");
        anchor = getPara("anchor", scope, null);
        onlyShowPreviousAndNext = getPara("onlyShowPreviousAndNext", scope, false);

        String previousText = getPara("previousText", scope, "上一页");
        String nextText = getPara("nextText", scope, "下一页");

        Page<?> page = getPage(env, scope, writer);

        int currentPage = page == null ? 1 : page.getPageNumber();
        int totalPage = page == null ? 1 : page.getTotalPage();

        if ((totalPage <= 0) || (currentPage > totalPage)) {
            return;
        }

        int startPage = currentPage - 4;
        if (startPage < 1) {
            startPage = 1;
        }
        int endPage = currentPage + 4;
        if (endPage > totalPage) {
            endPage = totalPage;
        }

        if (currentPage <= 8) {
            startPage = 1;
        }

        if ((totalPage - currentPage) < 8) {
            endPage = totalPage;
        }

        List<PaginateItem> pages = new ArrayList<PaginateItem>();
        if (currentPage == 1) {
            pages.add(new PaginateDirectiveBase.PaginateItem(previousClass + " " + disabledClass, "javascript:;", previousText));
        } else {
            pages.add(new PaginateDirectiveBase.PaginateItem(previousClass, getUrl(currentPage - 1), previousText));
        }

        if (currentPage > 8 && !onlyShowPreviousAndNext) {
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(1), "1"));
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(2), "2"));
            pages.add(new PaginateDirectiveBase.PaginateItem(disabledClass, "javascript:;", "..."));
        }

        if (!onlyShowPreviousAndNext) {
            for (int i = startPage; i <= endPage; i++) {
                if (currentPage == i) {
                    pages.add(new PaginateDirectiveBase.PaginateItem(activeClass, "javascript:;", i));
                } else {
                    pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(i), i));
                }
            }
        }

        if ((totalPage - currentPage) >= 8 && !onlyShowPreviousAndNext) {
            pages.add(new PaginateDirectiveBase.PaginateItem(disabledClass, "javascript:;", "..."));
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(totalPage - 1), totalPage - 1));
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(totalPage), totalPage));
        }

        if (currentPage == totalPage) {
            pages.add(new PaginateDirectiveBase.PaginateItem(nextClass + " " + disabledClass, "javascript:;", nextText));
        } else {
            pages.add(new PaginateDirectiveBase.PaginateItem(nextClass, getUrl(currentPage + 1), nextText));
        }

        scope.setLocal(getPageItemsName(), pages);

        renderBody(env, scope, writer);
    }


    public String getPrevious() {
        return previousClass;
    }

    public String getNext() {
        return nextClass;
    }

    public String getDisabled() {
        return disabledClass;
    }

    public String getAnchor() {
        return anchor;
    }


    protected abstract String getUrl(int pageNumber);

    protected abstract Page<?> getPage(Env env, Scope scope, Writer writer);


    protected String getPageItemsName() {
        return "pages";
    }


    @Override
    public boolean hasEnd() {
        return true;
    }


    public static class PaginateItem {
        private String style;
        private String url;
        private String text;

        public PaginateItem(String style, String url, String text) {
            this.style = style;
            this.url = url;
            this.text = text;
        }

        public PaginateItem(String style, String url, int text) {
            this.style = style;
            this.url = url;
            this.text = text + "";
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
