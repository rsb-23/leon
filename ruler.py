import os
import re

SRC_DIR = "core-domain/src/main/kotlin/com/svenjacobs/app/leon/core/domain/sanitizer"


def camel_to_snake(name):
    s1 = re.sub("(.)([A-Z][a-z]+)", r"\1_\2", name)
    return re.sub("([a-z0-9])([A-Z])", r"\1_\2", s1).lower()


def get_key(filename):
    # Remove extension and 'Test' suffix, then 'Sanitizer' suffix, then convert to snake_case
    base = filename.removesuffix("Sanitizer.kt")
    return camel_to_snake(base)


def extract_rule_from_file(filepath):
    with open(filepath, encoding="utf-8") as f:
        content = f.read()
    # remove everything before the first class declaration
    content = content.split("class ",1)[-1]

    # Try to match as property or constructor argument
    # type_match = re.search(r'(?:val|var|override\s+val|override\s+var)?\s*type\s*[:=]\s*"?([A-Za-z0-9_]+)"?', content)
    
    # Match parameters from RegexFactory.AllParameters, if AllParameters is used parameters will be [*]
    parameters_match = re.search(r"RegexFactory\.(AllParameters|[a-zA-Z]+\(\"([!-~]+)\"\))", content)
    # print(f"Parameters match: {parameters_match}")
    if parameters_match:
        # If ofParameter is used, we want to extract the parameter name
        if parameters_match.group(1).startswith(("ofParameter", "ofWildcardParameter")):
            parameters = parameters_match.group(2)
        else:
            parameters = "*"
        parameters_match = [f"{parameters}"]
    else:
        parameters_match = None

    domains_match = re.search(r"input\.matchesDomain\((?:\"|\')([^\"]+)(?:\"|\')\)", content)
    domain_regex_match = re.search(
        r"input\.matchesDomainRegex\((?:\"|\')([^\"]+)(?:\"|\')\)", content
    )

    config = {"type": "remove_params"}
    if parameters_match:
        config["parameters"] = parameters_match
    if domains_match:
        config["domains"] = domains_match.group(1).strip()
    if domain_regex_match:
        config["domain_regex"] = domain_regex_match.group(1).strip().replace("\\\\", "\\")

    return config


def extract_tests_from_file(filepath):
    with open(filepath, encoding="utf-8") as f:
        content = f.read()
    content = content.replace("\t", "")
    pattern = re.compile(r"sanitizer\((?P<input>[\s\S]*?)\)\s*shouldBe\s*(?P<expected>\"[^\"]*\")")
    pattern2 = re.compile(
        r"result\s*=\s*sanitizer\((?P<input>[\s\S]*?)\)[\s\S]*?result\s*shouldBe\s*(?P<expected>\"[^\"]*\")"
    )
    pattern3 = re.compile(
        r"val result\s*=\s*sanitizer\((?P<input>[\s\S]*?)\)[\s\S]*?result\s*shouldBe\s*(?P<expected>\"[^\"]*\")"
    )
    tests = []
    for pat in [pattern, pattern2, pattern3]:
        for match in pat.finditer(content):
            input_str = match.group("input")
            expected = match.group("expected")
            input_str = input_str.replace("\n", "").replace("+", "").replace('"', "").replace(" ", "")
            input_str = input_str.strip().strip(',')
            expected = expected.strip('"')
            if input_str and expected:
                tests.append({"input": input_str, "expected": expected})
    return tests


def main():
    rules = {}
    MAIN_DIR = "core-domain/src/main/kotlin/com/svenjacobs/app/leon/core/domain/sanitizer"
    TEST_DIR = "core-domain/src/test/kotlin/com/svenjacobs/app/leon/core/domain/sanitizer"

    for root, dirs, files in os.walk(MAIN_DIR):
        for dir in dirs:
            # loop through all files in all dir
            for subdir, _, subfiles in os.walk(os.path.join(root, dir)):
                for file in subfiles:
                    if file.endswith("Sanitizer.kt"):
                        key = get_key(file)
                        filepath = os.path.join(root, dir, file)
                        rule = extract_rule_from_file(filepath)
                        rules[key] = rule

                        # Try to find corresponding test file in test dir (same relative path, Test suffix)
                        rel_path = os.path.relpath(filepath, MAIN_DIR)
                        test_file = os.path.join(TEST_DIR, rel_path).replace(".kt", "Test.kt")
                        if not os.path.exists(test_file):
                            print(f"Test file not found for {key}: {rel_path}")
                            continue
                        tests = extract_tests_from_file(test_file)

                        if tests:
                            rules[key]["tests"] = tests

    # Sort rules by key
    rules = dict(sorted(rules.items()))

    # Write as JSON structure for rules.json
    import json

    with open("rules.json", "w", encoding="utf-8") as f:
        json.dump({"version": "1.0", "rules": rules}, f, indent=2, ensure_ascii=False)
    print("rules.json generated.")


if __name__ == "__main__":
    main()
