#!/usr/bin/env python3
"""Fail the build unless unit tests actually ran and all of them passed.

A Gradle test task reports success when it executes zero tests, which looks
exactly like a real green run. This turns that case into a failure.
"""
import glob
import sys
import xml.etree.ElementTree as ET

DEFAULT_PATTERN = "app/build/test-results/testDebugUnitTest/*.xml"


def main(pattern: str) -> int:
    files = glob.glob(pattern)
    if not files:
        print(f"No test result files matched '{pattern}'.", file=sys.stderr)
        return 1

    total = failures = skipped = 0
    for path in files:
        root = ET.parse(path).getroot()
        total += int(root.get("tests", 0))
        failures += int(root.get("failures", 0)) + int(root.get("errors", 0))
        skipped += int(root.get("skipped", 0))

    print(f"tests={total} failures={failures} skipped={skipped} files={len(files)}")

    if total == 0:
        print("Test task passed but executed no tests.", file=sys.stderr)
        return 1
    if failures:
        print(f"{failures} test(s) failed.", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1] if len(sys.argv) > 1 else DEFAULT_PATTERN))
