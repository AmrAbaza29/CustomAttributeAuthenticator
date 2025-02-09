<!DOCTYPE html>
<html>
<head>
    <title>Custom Attribute Login</title>
</head>
<body>
<#macro registrationLayout section>
    <#if section == "header">
        <h1>Custom Attribute Login</h1>
    <#elseif section == "form">
        <div>
            <#nested/>
        </div>
    </#if>
</#macro>
</body>
</html>