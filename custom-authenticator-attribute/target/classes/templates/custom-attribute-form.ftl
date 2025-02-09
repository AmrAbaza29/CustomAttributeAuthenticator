<#import "template.ftl" as layout>
<@layout.registrationLayout section="form">
    <form id="kc-form-login" action="${url.loginAction}" method="post">
        <#if errors??>
            <div class="alert alert-error">
                <#list errors as error>
                    <p>${error.message}</p>
                </#list>
            </div>
        </#if>
        <div class="${properties.kcFormGroupClass!}">
            <label for="attribute1">${attribute1Label!}</label>
            <input type="text" id="attribute1" name="attribute1" class="${properties.kcInputClass!}" value="${attribute1!}" autofocus/>
        </div>
        <div class="${properties.kcFormGroupClass!}">
            <label for="attribute2">${attribute2Label!}</label>
            <input type="text" id="attribute2" name="attribute2" class="${properties.kcInputClass!}" value="${attribute2!}"/>
        </div>
        <div class="${properties.kcFormGroupClass!}">
            <input type="submit" value="${msg("doLogIn")}" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!}"/>
        </div>
    </form>
</@layout.registrationLayout>