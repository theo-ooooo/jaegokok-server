/* 재고콕 — App shell (desktop sidebar + mobile tab bar) */

const NAV = [
  { id: "dash",      label: "대시보드",   sub: "한눈에 보는 재고 현황",   Icon: IconHome },
  { id: "products",  label: "상품 목록",   sub: "검색 · QR 발급",          Icon: IconBox },
  { id: "scan",      label: "입출고",     sub: "QR 스캔으로 기록",        Icon: IconScan },
  { id: "employees", label: "직원 관리",   sub: "계정 · 권한",             Icon: IconUsers },
];

function Toasts({ items }) {
  return (
    <div style={{ position: "absolute", left: 0, right: 0, bottom: 16, display: "flex", flexDirection: "column",
      alignItems: "center", gap: 8, pointerEvents: "none", zIndex: 80 }}>
      {items.map(t => (
        <div key={t.id} style={{ background: "var(--text-primary)", color: "#fff", padding: "11px 18px",
          borderRadius: "var(--r-pill)", fontSize: 13.5, fontWeight: 600, boxShadow: "var(--shadow-pop)",
          display: "flex", alignItems: "center", gap: 8, animation: "jk-toast .3s var(--ease-out)" }}>
          <span style={{ color: "var(--brand-soft)" }}><IconCheck size={16}/></span>{t.msg}
        </div>
      ))}
    </div>
  );
}

function App({ mode, store, startScreen, ownerName }) {
  const mobile = mode === "mobile";
  const [screen, setScreen] = React.useState(startScreen || "dash");
  const sProducts = React.useState(() => PRODUCTS.map(p => ({ ...p })));
  const sEmployees = React.useState(() => EMPLOYEES.map(e => ({ ...e })));
  const sActivity = React.useState(() => ACTIVITY.map(a => ({ ...a })));
  const [products, setProducts] = store ? [store.products, store.setProducts] : sProducts;
  const [employees, setEmployees] = store ? [store.employees, store.setEmployees] : sEmployees;
  const [activity, setActivity] = store ? [store.activity, store.setActivity] : sActivity;
  const owner = ownerName || "강경원";
  const [toasts, setToasts] = React.useState([]);
  const tid = React.useRef(0);

  const toast = (msg) => {
    const id = ++tid.current;
    setToasts(t => [...t, { id, msg }]);
    setTimeout(() => setToasts(t => t.filter(x => x.id !== id)), 2400);
  };

  const onRecord = (prod, type, qty) => {
    setProducts(ps => ps.map(p => p.id === prod.id
      ? { ...p, stock: type === "in" ? p.stock + qty : Math.max(0, p.stock - qty) } : p));
    setActivity(a => [{ type, name: prod.name, qty, who: owner, at: "방금" }, ...a].slice(0, 7));
    toast(`${prod.name} ${type === "in" ? "입고" : "출고"} ${qty}개 기록됨`);
  };
  const onAdd = (emp) => {
    const colors = ["#3A5BD9", "#1E9E6A", "#C9871F", "#D6453F", "#5C636E"];
    setEmployees(es => [...es, { ...emp, id: "E" + (es.length + 1), status: "invited", last: "초대됨",
      color: colors[es.length % colors.length] }]);
  };

  const cur = NAV.find(n => n.id === screen);
  const screenEl = {
    dash: <DashScreen products={products} activity={activity} mobile={mobile} go={setScreen}/>,
    products: <ProductsScreen products={products} mobile={mobile} toast={toast}/>,
    scan: <ScanScreen products={products} mobile={mobile} onRecord={onRecord}/>,
    employees: <EmployeesScreen employees={employees} mobile={mobile} onAdd={onAdd} toast={toast}/>,
  }[screen];

  /* ---------------- DESKTOP ---------------- */
  if (!mobile) {
    return (
      <div style={{ display: "flex", height: "100%", position: "relative", background: "var(--bg)" }}>
        <aside style={{ width: 244, flex: "none", borderRight: "1px solid var(--border)", display: "flex", flexDirection: "column", background: "var(--bg)" }}>
          <div style={{ padding: "22px 22px 18px", display: "flex", alignItems: "center", gap: 11, borderBottom: "1px solid var(--border)" }}>
            <LogoMark size={30}/>
            <div style={{ lineHeight: 1 }}>
              <div style={{ fontWeight: 900, fontSize: 19, letterSpacing: "-.02em" }}>재고콕<span style={{ color: "var(--brand)" }}>.</span></div>
              <div style={{ fontSize: 9.5, fontWeight: 700, letterSpacing: ".18em", color: "var(--text-tertiary)", marginTop: 4 }}>JAEGOKKOK</div>
            </div>
          </div>
          <nav style={{ padding: 12, display: "grid", gap: 2, flex: 1 }}>
            {NAV.map(({ id, label, Icon }) => {
              const on = screen === id;
              return (
                <button key={id} onClick={()=>setScreen(id)} style={{ display: "flex", alignItems: "center", gap: 12,
                  padding: "11px 13px", border: 0, borderRadius: "var(--r-2)", cursor: "pointer", textAlign: "left",
                  background: on ? "var(--bg-tint)" : "transparent", color: on ? "var(--brand)" : "var(--text-secondary)",
                  fontWeight: on ? 700 : 500, fontSize: 14.5, transition: "background .15s, color .15s" }}>
                  <Icon size={20}/>{label}
                </button>
              );
            })}
          </nav>
          <div style={{ padding: 12, borderTop: "1px solid var(--border)", display: "flex", alignItems: "center", gap: 11 }}>
            <Avatar name={owner} color="#3A5BD9"/>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontWeight: 700, fontSize: 13.5 }}>{owner} <span style={{ fontSize: 10, color: "var(--brand)", fontWeight: 800 }}>OWNER</span></div>
              <div className="t-mono" style={{ fontSize: 11 }}>{store && store.bizName ? store.bizName : "해와달 카페"}</div>
            </div>
            <button className="btn btn--quiet" style={{ padding: 6, minHeight: 0 }} title="로그아웃"><IconLogout size={18}/></button>
          </div>
        </aside>

        <div style={{ flex: 1, display: "flex", flexDirection: "column", minWidth: 0 }}>
          <header style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "18px 28px", borderBottom: "1px solid var(--border)" }}>
            <div>
              <h1 className="t-h2" style={{ margin: 0, fontSize: 20 }}>{cur.label}</h1>
              <div className="t-body" style={{ margin: 0, fontSize: 13 }}>{cur.sub}</div>
            </div>
            <div style={{ display: "flex", alignItems: "center", gap: 14 }}>
              <span className="t-mono" style={{ fontSize: 12 }}>2026.06.01 (월)</span>
              <button className="btn btn--ghost" style={{ padding: 9, minHeight: 0, position: "relative" }}>
                <IconBell size={20}/>
                <span style={{ position: "absolute", top: 7, right: 8, width: 7, height: 7, borderRadius: "50%", background: "var(--warn)", border: "1.5px solid #fff" }}/>
              </button>
            </div>
          </header>
          <main style={{ flex: 1, overflowY: "auto", padding: 28, background: "var(--bg-alt)" }}>{screenEl}</main>
        </div>
        <Toasts items={toasts}/>
      </div>
    );
  }

  /* ---------------- MOBILE ---------------- */
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", position: "relative", background: "var(--bg-alt)" }}>
      <header style={{ flex: "none", display: "flex", alignItems: "center", justifyContent: "space-between",
        padding: "14px 18px", background: "var(--bg)", borderBottom: "1px solid var(--border)" }}>
        <div style={{ display: "flex", alignItems: "center", gap: 9 }}>
          <LogoMark size={26}/>
          <span style={{ fontWeight: 900, fontSize: 18, letterSpacing: "-.02em" }}>{screen==="dash" ? <>재고콕<span style={{color:"var(--brand)"}}>.</span></> : cur.label}</span>
        </div>
        <button className="btn btn--quiet" style={{ padding: 7, minHeight: 0, position: "relative" }}>
          <IconBell size={21}/>
          <span style={{ position: "absolute", top: 6, right: 7, width: 7, height: 7, borderRadius: "50%", background: "var(--warn)", border: "1.5px solid #fff" }}/>
        </button>
      </header>

      <main style={{ flex: 1, overflowY: "auto", padding: 16, paddingBottom: 90 }}>{screenEl}</main>

      {/* bottom tab bar with center scan FAB */}
      <nav style={{ flex: "none", position: "relative", display: "flex", alignItems: "center", justifyContent: "space-around",
        background: "var(--bg)", borderTop: "1px solid var(--border)", paddingBottom: 6 }}>
        {[NAV[0], NAV[1]].map(({ id, label, Icon }) => (
          <TabBtn key={id} on={screen===id} label={label} onClick={()=>setScreen(id)}><Icon size={23}/></TabBtn>
        ))}
        <div style={{ width: 64, flex: "none" }}/>
        {[NAV[3]].map(({ id, label, Icon }) => (
          <TabBtn key={id} on={screen===id} label={label} onClick={()=>setScreen(id)}><Icon size={23}/></TabBtn>
        ))}
        {/* spacer to balance 4 items around center */}
        <TabBtn on={false} label="더보기" onClick={()=>toast("더보기")}><IconDots size={23}/></TabBtn>
        {/* center FAB */}
        <button onClick={()=>setScreen("scan")} aria-label="스캔" style={{ position: "absolute", left: "50%", top: -22, transform: "translateX(-50%)",
          width: 62, height: 62, borderRadius: "50%", border: "4px solid var(--bg)", cursor: "pointer",
          background: screen==="scan" ? "var(--brand-strong)" : "var(--brand)", color: "#fff",
          display: "flex", alignItems: "center", justifyContent: "center", boxShadow: "var(--shadow-glow)" }}>
          <IconScan size={28}/>
        </button>
      </nav>
      <Toasts items={toasts}/>
    </div>
  );
}

function TabBtn({ on, label, onClick, children }) {
  return (
    <button onClick={onClick} style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", gap: 3,
      padding: "9px 0 5px", border: 0, background: "transparent", cursor: "pointer",
      color: on ? "var(--brand)" : "var(--text-tertiary)", transition: "color .15s" }}>
      {children}
      <span style={{ fontSize: 10.5, fontWeight: on ? 700 : 500 }}>{label}</span>
    </button>
  );
}

Object.assign(window, { App });
