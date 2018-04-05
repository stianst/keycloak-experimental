<h1>Token</h1>

<form method="post">
    <label for="token">Token</label></br>
    <textarea id="token" name="token" rows="15" cols="100">${token!}</textarea><br/>
    <input type="submit" value="Submit">
</form>

<#if tokenParsed??>
    <h1>Result</h1>

    <h2>Header</h2>

    <pre>${header}</pre>

    <h2>Token</h2>

    <pre>${tokenParsed}</pre>

    <h2>Validation</h2>

    <#if valid>
      <p>Token is valid. Signed with <#if activeKey>active key<#else>passive key</#if>.</p>
    <#else>
      <p>Invalid token: ${error}</p>
    </#if>
</#if>