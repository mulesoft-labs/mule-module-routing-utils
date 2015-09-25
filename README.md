# mule-module-routing-utils

WELCOME
=======
The goal of this module to add small workarounds to the current use of choice in a mule application.
Right now, it has just one processor called `<routing-utils:detour ../>` that will emulate a single `<when .. />` of a `<mule:choise ../>`.

Usage
-----

### Simple usage

```xml
<set-payload value="before detour"/>
<routing-utils:detour when="#[message.outboundProperties['foo'] == 'bar']">
    <set-payload value="executed"/>
</routing-utils:detour>
```

Evaluates the `when` attribute to determine if has to execute the inner chain (in this case, the `<set-payload .../>` processor).

Parameters:
- *when* a MEL that can evaluate a range of expressions. It supports some base when types such as header, payload (payload type), regex, and wildcard.
- *nullReturnsTrue* if the evaluated MEL *when* is null, then the processor chain will be executed anyway. False by default.

Returns:
- The MuleMessage unmodified if the *when* parameter evaluates to false.
- The same MuleMessage but with it's payload changed to the result of the executed chain if the *when* parameter evaluates to true.

TESTING
=======

This  project also contains test classes that can be run as part of a test suite.

INSTALLATION
============
For MuleStudio
--------------
1. Clone the repo and execute a `mvn clean install`.
2. Go to <repo_folder>/mule-module-routing-utils/target and use the *UpdateSite.zip* to install the component in Studio.
https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/modules/mule-module-requester/1.3/mule-module-requester-1.3-studio-plugin.zip
2. Install it in MuleStudio as a regular update site from a file. The module will appear under the Components tab.

For Maven
---------
```xml
<dependency>
    <groupId>org.mule.modules</groupId>
    <artifactId>mule-module-routing-utils</artifactId>
    <version>1.0.0-SNAPSHOT</version>        
</dependency>
```  