/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.apidoc;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Path;
import com.jfinal.kit.StrKit;
import io.jboot.apidoc.annotation.ApiResp;
import io.jboot.apidoc.annotation.ApiResps;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.*;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class ApiDocUtil {


    public static String getControllerPath(Class<?> controllerClass) {
        RequestMapping rm = controllerClass.getAnnotation(RequestMapping.class);
        if (rm != null) {
            return AnnotationUtil.get(rm.value());
        }

        Path path = controllerClass.getAnnotation(Path.class);
        if (path != null) {
            return AnnotationUtil.get(path.value());
        }

        PostMapping pm = controllerClass.getAnnotation(PostMapping.class);
        if (pm != null) {
            return AnnotationUtil.get(pm.value());
        }

        GetMapping gm = controllerClass.getAnnotation(GetMapping.class);
        if (gm != null) {
            return AnnotationUtil.get(gm.value());
        }
        return null;
    }


    public static HttpMethod getControllerMethod(Class<?> controllerClass) {
        RequestMapping rm = controllerClass.getAnnotation(RequestMapping.class);
        if (rm != null) {
            return HttpMethod.ALL;
        }

        Path path = controllerClass.getAnnotation(Path.class);
        if (path != null) {
            return HttpMethod.ALL;
        }

        PostMapping pm = controllerClass.getAnnotation(PostMapping.class);
        if (pm != null) {
            return HttpMethod.POST;
        }

        GetMapping gm = controllerClass.getAnnotation(GetMapping.class);
        if (gm != null) {
            return HttpMethod.GET;
        }
        return HttpMethod.ALL;
    }


    public static HttpMethod[] getMethodHttpMethods(Method method, HttpMethod defaultMethod) {
        Set<HttpMethod> httpMethods = new HashSet<>();
        if (method.getAnnotation(GetRequest.class) != null) {
            httpMethods.add(HttpMethod.GET);
        }
        if (method.getAnnotation(PostRequest.class) != null) {
            httpMethods.add(HttpMethod.POST);
        }
        if (method.getAnnotation(PutRequest.class) != null) {
            httpMethods.add(HttpMethod.PUT);
        }
        if (method.getAnnotation(DeleteRequest.class) != null) {
            httpMethods.add(HttpMethod.DELETE);
        }
        if (method.getAnnotation(PatchRequest.class) != null) {
            httpMethods.add(HttpMethod.PATCH);
        }
        return httpMethods.isEmpty() ? new HttpMethod[]{defaultMethod} : httpMethods.toArray(new HttpMethod[]{});
    }


    private static final String SLASH = "/";

    public static String getActionKey(Method method, String controllerPath) {
        String methodName = method.getName();
        ActionKey ak = method.getAnnotation(ActionKey.class);
        String actionKey;
        if (ak != null) {
            actionKey = ak.value().trim();

            if (actionKey.startsWith(SLASH)) {
                //actionKey = actionKey
            } else if (actionKey.startsWith("./")) {
                actionKey = controllerPath + actionKey.substring(1);
            } else {
                actionKey = SLASH + actionKey;
            }
        } else if (methodName.equals("index")) {
            actionKey = controllerPath;
        } else {
            actionKey = controllerPath.equals(SLASH) ? SLASH + methodName : controllerPath + SLASH + methodName;
        }

        return actionKey;
    }


    public static List<ApiResponse> getApiResponseInMethod(Method method){

        List<ApiResponse> retList = new LinkedList<>();

        ApiResps apiResps = method.getAnnotation(ApiResps.class);
        if (apiResps != null) {
            for (ApiResp apiResp : apiResps.value()) {
                retList.add(new ApiResponse(apiResp));
            }
        }

        ApiResp apiResp = method.getAnnotation(ApiResp.class);
        if (apiResp != null) {
            retList.add(new ApiResponse(apiResp));
        }

        return retList;
    }



    public static String prettyJson(String json) {
        if (StrUtil.isBlank(json)) {
            return json;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(json, Feature.OrderedField);
        } catch (Exception e) {
            return json;
        }
        return JSONObject.toJSONString(jsonObject, true);
    }


    public static String getterMethod2Field(Method getterMethod) {
        String methodName = getterMethod.getName();
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return StrKit.firstCharToLowerCase(methodName.substring(3));
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return StrKit.firstCharToLowerCase(methodName.substring(2));
        }
        return null;
    }



}
