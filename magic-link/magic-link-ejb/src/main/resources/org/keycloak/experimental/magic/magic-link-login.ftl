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
                if (request.signRequests.length > 0) {
                    u2f.sign(
                        request.appId,
                        request.challenge,
                        request.signRequests,
                        function(data) {
                            if(data.errorCode) {
                                switch (data.errorCode) {
                                    case 4:
                                        alert("This device is not registered for this account.");
                                        break;

                                    default:
                                        alert("U2F failed with error code: " + data.errorCode);
                                }
                                return;
                            } else {
                                document.getElementById('tokenResponse').value = JSON.stringify(data);
                                document.getElementById('kc-u2f-login-form').submit();
                            }
                        }
                    );
                }
            }, 1000);
        </script>

        <p>Touch token to login</p>

        <form action="${url.loginAction}" class="${properties.kcFormClass!}" id="kc-u2f-login-form" method="post">
            <input type="hidden" name="tokenResponse" id="tokenResponse"/>

            <input style="display:none;" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doSubmit")}"/>
        </form>
    </#if>
</@layout.registrationLayout>