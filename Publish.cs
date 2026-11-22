#:package System.CommandLine@3.0.0
#:package CliWrap@3.10.5

using CliWrap;
using System.CommandLine;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Text.RegularExpressions;

RootCommand rootCommand = new("Publish a mod");

Option<string> modVersionOpt = new("--v", "-v")
{
    Required = true,
    Description = "The version of the mod to publish",
};
rootCommand.Add(modVersionOpt);
Option<string[]> mcVersionsOpt = new("--mc-versions", "-mc")
{
    Description = "The Minecraft versions to publish for. If not specified, all .pub.json files in the current directory will be used.",
    AllowMultipleArgumentsPerToken = true,
};
rootCommand.Add(mcVersionsOpt);
Option<ReleaseType> releaseTypeOpt = new("--release-type", "-rt")
{
    Description = "The release type for the current build. Defaults to Release.",
    AllowMultipleArgumentsPerToken = false,
};
rootCommand.Add(releaseTypeOpt);

var results = rootCommand.Parse(args);

var modVersion = results.GetRequiredValue(modVersionOpt);
var mcVersions = results.GetValue(mcVersionsOpt);
var releaseType = results.GetValue(releaseTypeOpt);
Statics.LoadDotEnv();

Statics.SetVar("version", modVersion);

if(mcVersions is null || mcVersions.Length == 0)
{
    var pubFiles = Directory.GetFiles("./", "*.pub.json");
    mcVersions = pubFiles.Select(file => Path.GetFileName(file).Replace(".pub.json", "")).ToArray();
}
foreach(var version in mcVersions)
{
    var filePath = $"./{version}.pub.json";
    if(!File.Exists(filePath))
    {
        Console.WriteLine($"File {filePath} does not exist. Skipping version {version}.");
        continue;
    }

    bool shouldSkip = false;
    var json = File.ReadAllText(filePath);
    var sourceDefinition = JsonSerializer.Deserialize(json, PublishCtx.Default.SourceDefinition);

    if(sourceDefinition == null)
    {
        Console.Error.WriteLine($"Failed to deserialize {filePath}");
        continue;
    }

    Console.WriteLine($"Version: {version}");
    foreach(var loader in sourceDefinition.MainFiles.Keys)
    {
        if(!File.Exists(sourceDefinition.MainFiles[loader]))
        {
            Console.Error.WriteLine($"Main file for {loader} does not exist: {sourceDefinition.MainFiles[loader]}");
            shouldSkip = true;
            break;
        }
        Console.WriteLine($"  Loader: {loader}, Main File: {sourceDefinition.MainFiles[loader]}");
        if(sourceDefinition.ExtraFiles.TryGetValue(loader, out var extraFiles))
        {
            // Check if all extra files exist
            if(extraFiles.Any(extraFile => !File.Exists(extraFile)))
            {
                Console.Error.WriteLine($"One or more extra files for {loader} do not exist: {string.Join(", ", extraFiles)}");
                shouldSkip = true;
                break;
            }
            // foreach(var extraFile in extraFiles)
            // {
            //     Console.WriteLine($"    Extra File: {extraFile}");
            // }
        }
        if(sourceDefinition.ChangelogFiles != null && sourceDefinition.ChangelogFiles.TryGetValue(loader, out var changelogFile))
        {
            if(!File.Exists(changelogFile))
            {
                Console.Error.WriteLine($"Changelog file for {loader} does not exist: {changelogFile}");
                shouldSkip = true;
                break;
            }
            Console.WriteLine($"    Changelog File: {changelogFile}");
        }
    }
    if(shouldSkip)
    {
        Console.ForegroundColor = ConsoleColor.Yellow;
        Console.Error.WriteLine($"Skipping version {version} due to missing files.");
        Console.ResetColor();
        continue;
    }
    Console.ForegroundColor = ConsoleColor.Green;
    Console.WriteLine($"Publishing {version} mod version {modVersion} to {sourceDefinition.PublishTo}...");
    Console.ResetColor();

    if(sourceDefinition.PublishTo.HasFlag(Hosts.Curseforge))
    {
        Console.WriteLine($"Publishing to Curseforge...");
    }
    if(sourceDefinition.PublishTo.HasFlag(Hosts.Modrinth))
    {
        await PublishToModrinth(sourceDefinition, modVersion, releaseType);
    }
    if(sourceDefinition.PublishTo.HasFlag(Hosts.GitHub))
    {
        await PublishToGitHub(sourceDefinition, modVersion, releaseType);
    }
}


async Task PublishToGitHub(SourceDefinition def, string version, ReleaseType releaseType)
{
    // Check if Git is installed
    var gitVersionOutput = new StringBuilder();
    var gitVersion = await Cli.Wrap("git")
        .WithArguments("--version")
        .WithStandardOutputPipe(PipeTarget.ToStringBuilder(gitVersionOutput))
        .ExecuteAsync();
    if(gitVersion.ExitCode != 0)
    {
        Console.Error.WriteLine("Git is not installed or not found in PATH. Please install Git to publish to GitHub.");
        return;
    }
    Console.WriteLine($"Found Git version: {gitVersionOutput.ToString().Trim()}");
    // Check if we have gh installed
    var ghVersionOutput = new StringBuilder();
    var ghVersion = await Cli.Wrap("gh")
        .WithArguments("--version")
        .WithStandardOutputPipe(PipeTarget.ToStringBuilder(ghVersionOutput))
        .ExecuteAsync();
    if(ghVersion.ExitCode != 0)
    {
        Console.Error.WriteLine("GitHub CLI (gh) is not installed or not found in PATH. Please install GitHub CLI to publish to GitHub.");
        return;
    }
    Console.WriteLine($"Found GitHub CLI version: {ghVersionOutput.ToString().Split(Environment.NewLine).FirstOrDefault("").Trim()}");
    // Check if a tag for the version already exists
    var tagCheckOutput = new StringBuilder();
    var tagCheck = await Cli.Wrap("git")
        .WithArguments($"tag --list v{version}")
        .WithStandardOutputPipe(PipeTarget.ToStringBuilder(tagCheckOutput))
        .ExecuteAsync();
    
    if(tagCheck.ExitCode != 0)
    {
        Console.Error.WriteLine($"Failed to check for existing Git tag for version {version}. Please check your Git repository.");
        return;
    }
    if(!string.IsNullOrWhiteSpace(tagCheckOutput.ToString()))
    {
        Console.ForegroundColor = ConsoleColor.Yellow;
        Console.WriteLine($"Git tag for version {version} already exists. Skipping GitHub release for this version.");
        Console.ResetColor();
        return;
    }
    var commandBuilder = new StringBuilder();
    commandBuilder.Append($"release create v{version} --title \"v{version}\"");
    if(releaseType != ReleaseType.Release)
    {
        commandBuilder.Append($" --prerelease");
    }
    foreach(var loader in def.MainFiles.Keys)
    {
        commandBuilder.Append($" \"{def.MainFiles[loader]}\"");
        if(def.ExtraFiles.TryGetValue(loader, out var extraFiles))
        {
            foreach(var extraFile in extraFiles)
            {
                commandBuilder.Append($" \"{extraFile}\"");
            }
        }
    }
    if(def.Changelog != null)
    {
        FileInfo changelogFileInfo = new(def.Changelog);
        commandBuilder.Append(" --notes-file \"" + changelogFileInfo.FullName + "\"");
    }
    //Create a new release on GitHub with the tag v{version}
    var releaseOutput = new StringBuilder();
    var release = await Cli.Wrap("gh")
        .WithValidation(CommandResultValidation.None)
        .WithArguments(commandBuilder.ToString())
        .WithStandardOutputPipe(PipeTarget.ToStream(Console.OpenStandardOutput()))
        .WithStandardErrorPipe(PipeTarget.ToStream(Console.OpenStandardError()))
        .ExecuteAsync();
    // If the release was successful, fetch the tags
    if(release.ExitCode == 0)
    {
        var fetchTags = await Cli.Wrap("git")
            .WithArguments("fetch --tags")
            .WithStandardErrorPipe(PipeTarget.ToStream(Console.OpenStandardError()))
            .ExecuteAsync();
        if(fetchTags.ExitCode != 0)
        {
            Console.Error.WriteLine($"Failed to fetch tags after creating GitHub release for version {version}. Please check your Git repository.");
            return;
        }
    }
}
async Task PublishToModrinth(SourceDefinition def, string version, ReleaseType releaseType)
{
    var projectId = def.ModrinthProjectId;
    if(string.IsNullOrWhiteSpace(projectId))
    {
        Console.Error.WriteLine("Modrinth project ID is not set in the source definition. Please set it to your Modrinth project ID to publish to Modrinth.");
        return;
    }
    var modrinthApiKey = Environment.GetEnvironmentVariable("MODRINTH_TOKEN");
    if(string.IsNullOrWhiteSpace(modrinthApiKey))
    {
        Console.Error.WriteLine("MODRINTH_TOKEN environment variable is not set. Please set it to your Modrinth API key to publish to Modrinth.");
        return;
    }
    HttpClient client = new()
    {
        DefaultRequestHeaders =
        {
            { "Authorization", $"Bearer {modrinthApiKey}" }
        }
    };
    HttpContent content = new MultipartContent();
    //var result = await client.PostAsync("https://api.modrinth.com/v2/version", content);
}
async Task PublishToCurseforge(SourceDefinition def, string version, ReleaseType releaseType)
{
    var projectId = def.CurseforgeProjectId;
    if(string.IsNullOrWhiteSpace(projectId))
    {
        Console.Error.WriteLine("Curseforge project ID is not set in the source definition. Please set it to your Curseforge project ID to publish to Curseforge.");
        return;
    }
    var curseforgeApiKey = Environment.GetEnvironmentVariable("CURSEFORGE_API_KEY");
    if(string.IsNullOrWhiteSpace(curseforgeApiKey))
    {
        Console.Error.WriteLine("CURSEFORGE_API_KEY environment variable is not set. Please set it to your Curseforge API key to publish to Curseforge.");
        return;
    }
    HttpClient client = new()
    {
        DefaultRequestHeaders =
        {
            { "X-Api-Key", curseforgeApiKey }
        }
    };
    HttpContent content = new MultipartContent();
    //var result = await client.PostAsync("https://minecraft.curseforge.com/api/projects/{projectId}/upload-file".Replace("{projectId}", "YOUR_PROJECT_ID"), content);
}


public class SourceDefinition : IJsonOnDeserialized
{
    public Hosts PublishTo { get; set; } = Hosts.All;
    public string? ModrinthProjectId { get; set; }
    public string? CurseforgeProjectId { get; set; }
    public Dictionary<ModLoader, string> MainFiles { get; set; } = [];
    public string? Changelog { get; set; }
    public Dictionary<ModLoader, string[]> ExtraFiles { get; set; } = [];
    public Dictionary<ModLoader, string>? ChangelogFiles { get; set; }

    public void OnDeserialized()
    {
        foreach(var entry in MainFiles)
        {
            var finalPath = entry.Value;
            var matches = Statics.TemplateVarRegex.Matches(finalPath);
            if(matches.Count > 0)
            {
                foreach(Match match in matches)
                {
                    var varName = match.Groups["varName"].Value;
                    var varValue = Statics.GetVar(varName);
                    if(varValue == null)
                        throw new Exception($"Environment variable {varName} is not set.");
                    finalPath = finalPath.Replace(match.Value, varValue);
                }
            }
            MainFiles[entry.Key] = finalPath;
        }
        foreach(var entry in ExtraFiles)
        {
            var finalPaths = entry.Value.Select(path =>
            {
                var finalPath = path;
                var matches = Statics.TemplateVarRegex.Matches(finalPath);
                if(matches.Count > 0)
                {
                    foreach(Match match in matches)
                    {
                        var varName = match.Groups["varName"].Value;
                        var varValue = Statics.GetVar(varName);
                        if(varValue == null)
                            throw new Exception($"Environment variable {varName} is not set.");
                        finalPath = finalPath.Replace(match.Value, varValue);
                    }
                }
                return finalPath;
            }).ToArray();
            ExtraFiles[entry.Key] = finalPaths;
        }
        foreach(var entry in ChangelogFiles ?? new Dictionary<ModLoader, string>())
        {
            var finalPath = entry.Value;
            var matches = Statics.TemplateVarRegex.Matches(finalPath);
            if(matches.Count > 0)
            {
                foreach(Match match in matches)
                {
                    var varName = match.Groups["varName"].Value;
                    var varValue = Statics.GetVar(varName);
                    if(varValue == null)
                        throw new Exception($"Environment variable {varName} is not set.");
                    finalPath = finalPath.Replace(match.Value, varValue);
                }
            }
            ChangelogFiles![entry.Key] = finalPath;
        }
    }
}

[JsonConverter(typeof(JsonStringEnumConverter<ModLoader>))]
public enum ModLoader
{
    Common,
    NeoForge,
    Fabric,
    Forge
}

[Flags]
[JsonConverter(typeof(JsonStringEnumConverter<Hosts>))]
public enum Hosts
{
    None = 0,
    GitHub = 1 << 0,
    Curseforge = 1 << 1,
    Modrinth = 1 << 2,
    All = GitHub | Curseforge | Modrinth
}
[JsonConverter(typeof(JsonStringEnumConverter<ReleaseType>))]
public enum ReleaseType
{
    Release,
    PreRelease,
    Beta,
    Alpha
}

public class CurseforgeUploadMetadata
{
    public string Changelog { get; set; } = string.Empty;
    public ChangelogTypeEnum ChangelogType { get; set; } = ChangelogTypeEnum.Text;
    public string? DisplayName { get; set; }
    public int? ParentFileID { get; set; }
    public List<int>? GameVersions { get; set; }
    public List<string>? GameVersionNames { get; set; }
    public string ReleaseType { get; set; } = "release";
    public bool IsMarkedForManualRelease { get; set; } = false;
    public ModRelations? Relations { get; set; }
    [JsonConverter(typeof(JsonStringEnumConverter<ChangelogTypeEnum>))]
    public enum ChangelogTypeEnum
    {
        Text,
        Html,
        Markdown
    }
    public class ModRelations
    {
        public List<ProjectRelation> Projects { get; set; } = [];
    }
    public class ProjectRelation
    {
        public string Slug { get; set; } = string.Empty;
        public string? ProjectID { get; set; }
        //[JsonConverter(typeof(JsonStringEnumConverter))]
        public List<ProjectType> Type { get; set; } = [];
    }
    [JsonConverter(typeof(JsonStringEnumConverter<ProjectType>))]
    public enum ProjectType
    {
        EmbeddedLibrary,
        Incompatible,
        OptionalDependency,
        RequiredDependency,
        Tool
    }
}

[JsonSerializable(typeof(SourceDefinition))]
//[JsonSerializable(typeof(ModLoader))]
//[JsonSerializable(typeof(Targets))]
[JsonSerializable(typeof(CurseforgeUploadMetadata))]
[JsonSerializable(typeof(CurseforgeUploadMetadata.ModRelations))]
[JsonSerializable(typeof(CurseforgeUploadMetadata.ProjectRelation))]
[JsonSourceGenerationOptions(
    PropertyNameCaseInsensitive = true,
    AllowTrailingCommas = true,
    ReadCommentHandling = JsonCommentHandling.Skip
)]
public partial class PublishCtx : JsonSerializerContext;

public static partial class Statics
{
    [GeneratedRegex(@"\$\{(?<varName>[^}]+)\}", RegexOptions.Compiled)]
    public static partial Regex TemplateVarRegex { get; }
    private static Dictionary<string, string> _envVarCache = new();

    public static void LoadDotEnv(string? path = null)
    {
        if(path == null)
            path = Path.Combine(Directory.GetCurrentDirectory(), ".env");
        if(!File.Exists(path))
            return;
        var lines = File.ReadAllLines(path);
        foreach(var line in lines)
        {
            var trimmedLine = line.Trim();
            if(string.IsNullOrWhiteSpace(trimmedLine) || trimmedLine.StartsWith("#"))
                continue;
            var parts = trimmedLine.Split('=', 2);
            if(parts.Length != 2)
                continue;
            var key = parts[0].Trim();
            var value = parts[1].Trim();
            Environment.SetEnvironmentVariable(key, value);
        }
    }

    public static string? GetVar(string varName)
    {
        if(_envVarCache.TryGetValue(varName, out var value))
        {
            return value;
        }
        else
        {
            value = Environment.GetEnvironmentVariable(varName);
            _envVarCache[varName] = value ?? string.Empty;
            return value;
        }
    }
    public static void SetVar(string varName, string value)
    {
        _envVarCache[varName] = value;
    }
}