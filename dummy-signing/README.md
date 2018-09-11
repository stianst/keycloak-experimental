# What's missing from SPIs

* Can't introduce new AlgorithmType
* Custom key providers can't be viewed in realm key active/all list in admin console
* Ability to set signature algorithm for realm and for clients
* TokenSignatureProviderFactory shouldn't extend ComponentFactory. This is when you want to be able to create multiple instances of a provider for a realm with different configuration. It complicates setup in admin console and also everything around how they are used. This is not needed.