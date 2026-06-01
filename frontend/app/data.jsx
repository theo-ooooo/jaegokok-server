/* 재고콕 — sample data */
const PRODUCTS = [
  { id: "P01", name: "콜드브루 원두 1kg",      sku: "SKU-00482", cat: "원두",   stock: 4,   safe: 20, price: 32000 },
  { id: "P02", name: "종이컵 16oz (1000입)",   sku: "SKU-00121", cat: "포장재", stock: 38,  safe: 15, price: 41000 },
  { id: "P03", name: "시럽 바닐라 750ml",       sku: "SKU-00310", cat: "부자재", stock: 0,   safe: 6,  price: 9800 },
  { id: "P04", name: "원두 에티오피아 1kg",     sku: "SKU-00488", cat: "원두",   stock: 27,  safe: 12, price: 38000 },
  { id: "P05", name: "테이크아웃 캐리어",       sku: "SKU-00150", cat: "포장재", stock: 9,   safe: 25, price: 15000 },
  { id: "P06", name: "우유 1L (멸균)",          sku: "SKU-00207", cat: "부자재", stock: 52,  safe: 20, price: 2400 },
  { id: "P07", name: "드립백 필터 (200매)",     sku: "SKU-00133", cat: "포장재", stock: 14,  safe: 10, price: 6800 },
  { id: "P08", name: "원두 콜롬비아 1kg",       sku: "SKU-00491", cat: "원두",   stock: 3,   safe: 12, price: 35000 },
];

const EMPLOYEES = [
  { id: "E01", name: "강경원", role: "OWNER",    email: "owner@store.kr",  status: "active", last: "방금 전",   color: "#3A5BD9" },
  { id: "E02", name: "김민재", role: "EMPLOYEE", email: "minjae@store.kr", status: "active", last: "12분 전",  color: "#1E9E6A" },
  { id: "E03", name: "이서윤", role: "EMPLOYEE", email: "seoyun@store.kr", status: "active", last: "1시간 전", color: "#C9871F" },
  { id: "E04", name: "박도현", role: "EMPLOYEE", email: "dohyun@store.kr", status: "invited",last: "초대됨",   color: "#5C636E" },
];

const ACTIVITY = [
  { type: "in",  name: "원두 에티오피아 1kg", qty: 10, who: "김민재", at: "09:24" },
  { type: "out", name: "종이컵 16oz",         qty: 4,  who: "이서윤", at: "09:02" },
  { type: "out", name: "콜드브루 원두 1kg",   qty: 2,  who: "김민재", at: "08:47" },
  { type: "in",  name: "우유 1L",             qty: 24, who: "강경원", at: "08:30" },
  { type: "out", name: "드립백 필터",         qty: 6,  who: "이서윤", at: "어제" },
];

const won = (n) => "₩" + n.toLocaleString("ko-KR");
const stockState = (p) => p.stock === 0 ? "out" : p.stock < p.safe ? "low" : "ok";

Object.assign(window, { PRODUCTS, EMPLOYEES, ACTIVITY, won, stockState });
