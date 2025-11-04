# WaybackGUI

> Advanced reconnaissance tool for Wayback Machine data extraction

A stealthy Java-based tool for extracting historical URL data from Wayback Machine archives. Designed for security researchers and penetration testers.

![Java](https://img.shields.io/badge/Java-17+-red?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

## 🚀 Features

- **Multiple Query Modes**: Domain, wildcard, specific paths, file extensions
- **Stealth Operation**: Rotating user agents, random delays, behavioral obfuscation
- **Advanced Bypass**: WAF evasion, rate limit countermeasures, fingerprint rotation
- **Real-time Results**: Stream data with progress tracking
- **Export Capability**: Save results to file

## 🛠️ Quick Start

### Prerequisites
- Java 17 or higher
- FlatLaf library

## 🎯 Usage

1. **Enter target domain** (example.com)
2. **Select query mode**:
    - Main domain
    - Wildcard subdomains
    - Specific paths
    - Sensitive files
    - Date ranges

3. **Start probe** and monitor real-time results

## ⚡ Query Modes

| Mode | Description | Use Case |
|------|-------------|----------|
| Main Domain | Target domain only | Initial recon |
| Wildcard | All subdomains | Expanded attack surface |
| Specific Path | Defined paths | Targeted searching |
| Sensitive Files | Configs, backups, logs | Critical data exposure |
| Date Ranges | Historical periods | Timeline analysis |

## 🔧 Advanced Features

### Stealth Technology
- **User Agent Rotation**: 10+ realistic browser fingerprints
- **Intelligent Delays**: Time-based and adaptive throttling
- **Header Randomization**: Varied Accept-Language, Cache-Control, Referer
- **Behavioral Obfuscation**: Human-like request patterns

### Anti-Detection
- **WAF Bypass**: Cloudflare evasion techniques
- **Rate Limit Handling**: Exponential backoff with jitter
- **TLS Fingerprint Rotation**: JA3 randomization through UA variation
- **Session Management**: Dynamic request signatures

## 🎨 UI Overview

![ui](https://github.com/hangga/Wayback-GUI/blob/main/Wayback_GUI.gif?raw=true)

## 📊 Output Example

```
▸ Target: example.com
▸ Mode: Sensitive file extensions  
▸ Stealth Mode: ACTIVE
▸ Results: 247 URLs extracted
▸ Status: MISSION ACCOMPLISHED - TARGET PENETRATED
```

## 🛡️ Use Cases

- **Bug Bounty**: Discover hidden endpoints and assets
- **Penetration Testing**: Map historical attack surface
- **Threat Intelligence**: Investigate past compromises
- **Digital Forensics**: Reconstruct website history
- **Asset Discovery**: Find forgotten subdomains and files

## 🔍 Reconnaissance Capabilities

### Passive Intelligence Gathering
- Historical URL enumeration
- Subdomain discovery
- File and directory mining
- Technology fingerprinting
- Timeline analysis

### Security Assessment
- Exposed config files detection
- Backup file identification
- Admin interface discovery
- API endpoint mapping
- Attack surface calculation

## ⚠️ Legal Notice

This tool is intended for:
- Security research
- Authorized penetration testing
- Educational purposes
- Bug bounty programs (where permitted)

Ensure you have proper authorization before scanning any targets. Respect robots.txt and terms of service.

## 🐛 Issues

Report bugs and feature requests via GitHub Issues.

## 📄 License

MIT License - see LICENSE file for details.

---

**Disclaimer**: Use responsibly. Authors not liable for misuse. Always obtain proper authorization before testing.
```

## Key Updates Made:

1. **Removed Proxy Sections** - No more proxy configuration mentions
2. **Enhanced Stealth Features** - Emphasized the built-in anti-detection
3. **Streamlined Setup** - Cleaner installation without proxy setup steps
4. **Focus on Core Tech** - Highlighted the advanced bypass techniques that don't require proxies
5. **Simplified UI Description** - Removed proxy status from UI overview

The README now focuses purely on the tool's sophisticated built-in evasion capabilities without external dependencies. Clean, professional, and hacker-style! 🚀