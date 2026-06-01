/* 재고콕 — Icons (sharp 1.75px stroke, matches DS hard-edge aesthetic) */
const I = ({ d, size = 22, sw = 1.75, fill = "none", children, vb = "0 0 24 24", style }) =>
  React.createElement("svg", { width: size, height: size, viewBox: vb, fill, stroke: "currentColor",
    strokeWidth: sw, strokeLinecap: "round", strokeLinejoin: "round", style }, children || (d && React.createElement("path", { d })));

const IconHome = (p) => <I {...p}><path d="M3 10.5 12 4l9 6.5"/><path d="M5 9.5V20h14V9.5"/><path d="M9.5 20v-5h5v5"/></I>;
const IconBox = (p) => <I {...p}><path d="M3.5 7.5 12 3l8.5 4.5v9L12 21l-8.5-4.5z"/><path d="M3.5 7.5 12 12l8.5-4.5"/><path d="M12 12v9"/></I>;
const IconScan = (p) => <I {...p}><path d="M4 8V5.5A1.5 1.5 0 0 1 5.5 4H8"/><path d="M16 4h2.5A1.5 1.5 0 0 1 20 5.5V8"/><path d="M20 16v2.5a1.5 1.5 0 0 1-1.5 1.5H16"/><path d="M8 20H5.5A1.5 1.5 0 0 1 4 18.5V16"/><path d="M4 12h16"/></I>;
const IconUsers = (p) => <I {...p}><circle cx="9" cy="8" r="3.2"/><path d="M3.5 19c0-3 2.5-5 5.5-5s5.5 2 5.5 5"/><path d="M16 5.2A3 3 0 0 1 16 11"/><path d="M17.5 14c2.2.4 4 2.3 4 5"/></I>;
const IconSearch = (p) => <I {...p}><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></I>;
const IconPlus = (p) => <I {...p}><path d="M12 5v14M5 12h14"/></I>;
const IconMinus = (p) => <I {...p}><path d="M5 12h14"/></I>;
const IconDownload = (p) => <I {...p}><path d="M12 4v11"/><path d="m7.5 10.5 4.5 4.5 4.5-4.5"/><path d="M5 20h14"/></I>;
const IconIn = (p) => <I {...p}><path d="M12 20V6"/><path d="m6 12 6-6 6 6"/></I>;
const IconOut = (p) => <I {...p}><path d="M12 4v14"/><path d="m6 12 6 6 6-6"/></I>;
const IconBell = (p) => <I {...p}><path d="M6 9a6 6 0 0 1 12 0c0 5 2 6 2 6H4s2-1 2-6"/><path d="M10 20a2 2 0 0 0 4 0"/></I>;
const IconCheck = (p) => <I {...p}><path d="m5 12.5 4.5 4.5L19 7"/></I>;
const IconChevR = (p) => <I {...p}><path d="m9 6 6 6-6 6"/></I>;
const IconChevD = (p) => <I {...p}><path d="m6 9 6 6 6-6"/></I>;
const IconX = (p) => <I {...p}><path d="M6 6l12 12M18 6 6 18"/></I>;
const IconFilter = (p) => <I {...p}><path d="M3 5h18M6 12h12M10 19h4"/></I>;
const IconTrend = (p) => <I {...p}><path d="m4 16 5-5 4 4 7-7"/><path d="M16 8h4v4"/></I>;
const IconQr = (p) => <I {...p}><rect x="3.5" y="3.5" width="7" height="7"/><rect x="3.5" y="13.5" width="7" height="7"/><rect x="13.5" y="3.5" width="7" height="7"/><path d="M13.5 13.5h3v3M20.5 13.5v7M16.5 20.5h4M13.5 20.5h0"/></I>;
const IconDots = (p) => <I {...p}><circle cx="5" cy="12" r="1.4" fill="currentColor" stroke="none"/><circle cx="12" cy="12" r="1.4" fill="currentColor" stroke="none"/><circle cx="19" cy="12" r="1.4" fill="currentColor" stroke="none"/></I>;
const IconLogout = (p) => <I {...p}><path d="M14 4H6v16h8"/><path d="M10 12h10"/><path d="m17 8 4 4-4 4"/></I>;
const IconClock = (p) => <I {...p}><circle cx="12" cy="12" r="8"/><path d="M12 8v4l3 2"/></I>;
const IconMail = (p) => <I {...p}><rect x="3" y="5" width="18" height="14" rx="1"/><path d="m3.5 6.5 8.5 6 8.5-6"/></I>;

const LogoMark = ({ size = 30, light = false }) => (
  <svg width={size} height={size} viewBox="0 0 64 64" aria-label="재고콕">
    <rect x="7" y="7" width="50" height="50" rx="6" fill={light ? "#fff" : "var(--brand)"}/>
    <path d="M19 33 l9 9 l17 -19" fill="none" stroke={light ? "var(--brand)" : "#fff"} strokeWidth="5.5" strokeLinecap="round" strokeLinejoin="round"/>
  </svg>
);

Object.assign(window, {
  IconHome, IconBox, IconScan, IconUsers, IconSearch, IconPlus, IconMinus, IconDownload,
  IconIn, IconOut, IconBell, IconCheck, IconChevR, IconChevD, IconX, IconFilter, IconTrend,
  IconQr, IconDots, IconLogout, IconClock, IconMail, LogoMark
});
