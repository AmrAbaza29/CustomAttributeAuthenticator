<#import "template.ftl" as layout>
<@layout.registrationLayout section="form">
    <div>
        <h2>User Data</h2>
        <p>Email: ${email}</p>
        <p>Contract Number: ${customAttr1}</p>
        <p>Registration Form Number: ${customAttr2}</p>
    </div>
    <form id="kc-form-login" action="${url.loginAction}" method="post">
        <div class="${properties.kcFormGroupClass!}">
            <input type="submit" value="Continue" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!}"/>
        </div>
    </form>
</@layout.registrationLayout>