<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "title">
        u2f
    <#elseif section = "header">
        u2f
    <#elseif section = "form">
        <script src="${url.resourcesPath}/u2f-api-1.1.js"></script>

        <script>
            var request = ${request?no_esc};
            setTimeout(function() {
                u2f.register(
                    request.appId,
                    request.registerRequests,
                    request.registeredKeys,
                    function(data) {
                        var form = document.getElementById('kc-u2f-settings-form');
                        var reg = document.getElementById('tokenResponse');
                        if(data.errorCode) {
                            switch (data.errorCode) {
                                case 4:
                                    alert("This device is already registered.");
                                    break;

                                default:
                                    alert("U2F failed with error: " + data.errorCode);
                            }
                        } else {
                            reg.value=JSON.stringify(data);
                            form.submit();
                        }
                    }
                );
            }, 1000);
        </script>

        <p>Touch token to register</p>

        <form action="${url.loginAction}" class="${properties.kcFormClass!}" id="kc-u2f-settings-form" method="post">
            <input type="hidden" name="tokenResponse" id="tokenResponse"/>

            <input style="display:none;" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doSubmit")}"/>
        </form>
    </#if>
</@layout.registrationLayout>