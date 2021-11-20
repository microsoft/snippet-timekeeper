# Snippet

> Snippet is a small extensible library to measure execution times of the code sections
> in a way that does not compromise with the readability and can be shipped to production 
> without any additional setup.

> New behaviors can be added in the library by extending 
> MeasuredExecutionPath - The code path the does the measurement code spans
> ReleaseExecutionPath - A no-op path (default path) that is usually installed in the release variants.

## Usage

Two easy steps:

1. Install the `MeasuredExecutionPath` instance you want in the `onCreate` of your application class.
2. Set the filter that you would like to use in the log cat.   
3. Set the flags that determine the amount of verbose in the logs.
    
   `if(BuildConfig.DEBUG) { `
        `Snippet.install(new Snippet.MeasuredExecutionPath());`
        `Snippet.newFilter("SomeFilter");`
        `Snippet.addFlag(Snippet.FLAG_METADATA_LINE | Snippet.FLAG_METADATA_THREAD_INFO);`
  `}` 
   `
Check out the sample app in `app/` to see it in action.


## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Trademarks

This project may contain trademarks or logos for projects, products, or services. Authorized use of Microsoft 
trademarks or logos is subject to and must follow 
[Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).
Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion or imply Microsoft sponsorship.
Any use of third-party trademarks or logos are subject to those third-party's policies.
