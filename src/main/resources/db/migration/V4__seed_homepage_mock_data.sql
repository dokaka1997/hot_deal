-- Seed comprehensive mock data for homepage sections (hot deals, flash sale, coupons).
-- This migration is idempotent and safe to run on existing environments.

INSERT INTO source (
    code,
    name,
    type,
    status,
    base_url,
    schedule_cron,
    rate_limit_per_minute,
    metadata
)
VALUES
    ('SHOPEE', 'Shopee Mall', 'API', 'ACTIVE', 'https://shopee.vn', '0 */10 * * * *', 240, '{"seed":"v4","channel":"marketplace"}'::jsonb),
    ('LAZADA', 'Lazada Mall', 'API', 'ACTIVE', 'https://www.lazada.vn', '0 */10 * * * *', 240, '{"seed":"v4","channel":"marketplace"}'::jsonb),
    ('TIKI', 'Tiki Trading', 'API', 'ACTIVE', 'https://tiki.vn', '0 */10 * * * *', 240, '{"seed":"v4","channel":"marketplace"}'::jsonb),
    ('FPTSHOP', 'FPT Shop', 'API', 'ACTIVE', 'https://fptshop.com.vn', '0 */15 * * * *', 180, '{"seed":"v4","channel":"retail"}'::jsonb),
    ('CELLPHONES', 'CellphoneS', 'API', 'ACTIVE', 'https://cellphones.com.vn', '0 */15 * * * *', 180, '{"seed":"v4","channel":"retail"}'::jsonb),
    ('DIENMAYXANH', 'Dien May Xanh', 'API', 'ACTIVE', 'https://www.dienmayxanh.com', '0 */20 * * * *', 120, '{"seed":"v4","channel":"retail"}'::jsonb),
    ('GUARDIAN', 'Guardian', 'API', 'ACTIVE', 'https://guardian.com.vn', '0 */20 * * * *', 120, '{"seed":"v4","channel":"retail"}'::jsonb),
    ('WATCHSTORE', 'WatchStore', 'API', 'ACTIVE', 'https://watchstore.vn', '0 */20 * * * *', 120, '{"seed":"v4","channel":"retail"}'::jsonb)
ON CONFLICT (code)
DO UPDATE SET
    name = EXCLUDED.name,
    type = EXCLUDED.type,
    status = EXCLUDED.status,
    base_url = EXCLUDED.base_url,
    schedule_cron = EXCLUDED.schedule_cron,
    rate_limit_per_minute = EXCLUDED.rate_limit_per_minute,
    metadata = EXCLUDED.metadata,
    updated_at = NOW();

WITH seed_products(canonical_sku, name, brand, category, image_url) AS (
    VALUES
        ('IP15PM-256', 'iPhone 15 Pro Max 256GB', 'Apple', 'phone', 'https://images.unsplash.com/photo-1695669206831-f65f7c9ef2f3?auto=format&fit=crop&w=1200&q=80'),
        ('S24U-256', 'Samsung Galaxy S24 Ultra 256GB', 'Samsung', 'phone', 'https://images.unsplash.com/photo-1610792516307-ea5acd9c3b00?auto=format&fit=crop&w=1200&q=80'),
        ('TUF-A15', 'Laptop ASUS TUF Gaming A15', 'ASUS', 'laptop', 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?auto=format&fit=crop&w=1200&q=80'),
        ('MBA-M3-13', 'MacBook Air M3 13 inch', 'Apple', 'laptop', 'https://images.unsplash.com/photo-1517336714739-489689fd1ca8?auto=format&fit=crop&w=1200&q=80'),
        ('APRO2-USBC', 'AirPods Pro 2 USB-C', 'Apple', 'audio', 'https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?auto=format&fit=crop&w=1200&q=80'),
        ('SONY-XM5', 'Sony WH-1000XM5', 'Sony', 'audio', 'https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=1200&q=80'),
        ('NIKE-AF1', 'Nike Air Force 1 07', 'Nike', 'fashion', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=1200&q=80'),
        ('ADI-CAMPUS', 'Adidas Campus 00s', 'Adidas', 'fashion', 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?auto=format&fit=crop&w=1200&q=80'),
        ('GSHOCK-2100', 'Casio G-Shock GA-2100', 'Casio', 'watch', 'https://images.unsplash.com/photo-1523170335258-f5ed11844a49?auto=format&fit=crop&w=1200&q=80'),
        ('PHILIPS-AF62', 'Noi chien khong dau Philips 6.2L', 'Philips', 'home', 'https://images.unsplash.com/photo-1585238342024-78d387f4a707?auto=format&fit=crop&w=1200&q=80'),
        ('XIAOMI-S10P', 'Robot hut bui Xiaomi S10+', 'Xiaomi', 'home', 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?auto=format&fit=crop&w=1200&q=80'),
        ('LRP-B5', 'Serum La Roche-Posay B5+', 'La Roche-Posay', 'beauty', 'https://images.unsplash.com/photo-1570194065650-d99fb4bedf0a?auto=format&fit=crop&w=1200&q=80')
)
INSERT INTO product (
    canonical_sku,
    name,
    normalized_name,
    brand,
    category,
    image_url,
    fingerprint,
    attributes,
    active
)
SELECT
    sp.canonical_sku,
    sp.name,
    LOWER(REGEXP_REPLACE(sp.name, '\s+', ' ', 'g')),
    sp.brand,
    sp.category,
    sp.image_url,
    md5(sp.canonical_sku),
    jsonb_build_object(
        'seed', 'v4',
        'uiGroup', 'homepage'
    ),
    TRUE
FROM seed_products sp
ON CONFLICT (canonical_sku)
DO UPDATE SET
    name = EXCLUDED.name,
    normalized_name = EXCLUDED.normalized_name,
    brand = EXCLUDED.brand,
    category = EXCLUDED.category,
    image_url = EXCLUDED.image_url,
    attributes = EXCLUDED.attributes,
    active = EXCLUDED.active,
    updated_at = NOW();

WITH seed_deal_base (
    source_code,
    product_sku,
    source_seed,
    title,
    description,
    brand,
    category,
    currency,
    base_original_price,
    base_discount_percent,
    base_score
) AS (
    VALUES
        ('FPTSHOP', 'IP15PM-256', 'IP15PM', 'iPhone 15 Pro Max 256GB', 'Flagship iPhone gia tot tu nha ban le uy tin.', 'Apple', 'phone', 'VND', 34990000::numeric, 14::numeric, 90::numeric),
        ('TIKI', 'S24U-256', 'S24U', 'Samsung Galaxy S24 Ultra 256GB', 'Dien thoai flagship Android giam gia theo gio.', 'Samsung', 'phone', 'VND', 31990000::numeric, 16::numeric, 88::numeric),
        ('LAZADA', 'TUF-A15', 'TUFA15', 'Laptop ASUS TUF Gaming A15', 'Laptop gaming gia re cho hoc tap va giai tri.', 'ASUS', 'laptop', 'VND', 23990000::numeric, 18::numeric, 86::numeric),
        ('CELLPHONES', 'MBA-M3-13', 'MBAM3', 'MacBook Air M3 13 inch', 'MacBook Air chip M3 pin trau giam gia trong tuan.', 'Apple', 'laptop', 'VND', 30990000::numeric, 10::numeric, 85::numeric),
        ('SHOPEE', 'APRO2-USBC', 'APRO2', 'AirPods Pro 2 USB-C', 'Tai nghe true wireless chong on chu dong.', 'Apple', 'audio', 'VND', 6290000::numeric, 24::numeric, 93::numeric),
        ('SHOPEE', 'SONY-XM5', 'SONYXM5', 'Sony WH-1000XM5', 'Tai nghe over-ear premium danh cho dan van phong.', 'Sony', 'audio', 'VND', 9990000::numeric, 28::numeric, 95::numeric),
        ('SHOPEE', 'NIKE-AF1', 'NIKEAF1', 'Nike Air Force 1 07', 'Giay sneaker ban chay theo xu huong duong pho.', 'Nike', 'fashion', 'VND', 2390000::numeric, 30::numeric, 84::numeric),
        ('LAZADA', 'ADI-CAMPUS', 'ADICAMPUS', 'Adidas Campus 00s', 'Giay lifestyle tre trung giam gia cuoi tuan.', 'Adidas', 'fashion', 'VND', 2590000::numeric, 27::numeric, 82::numeric),
        ('WATCHSTORE', 'GSHOCK-2100', 'GSHOCK2100', 'Casio G-Shock GA-2100', 'Dong ho ben bi nhieu mau hot.', 'Casio', 'watch', 'VND', 3990000::numeric, 21::numeric, 80::numeric),
        ('DIENMAYXANH', 'PHILIPS-AF62', 'PHILIPSAF62', 'Noi chien khong dau Philips 6.2L', 'Do gia dung can thiet cho gia dinh hien dai.', 'Philips', 'home', 'VND', 4290000::numeric, 26::numeric, 79::numeric),
        ('DIENMAYXANH', 'XIAOMI-S10P', 'XIAOMIS10P', 'Robot hut bui Xiaomi S10+', 'Robot hut bui thong minh ket noi app.', 'Xiaomi', 'home', 'VND', 12990000::numeric, 22::numeric, 83::numeric),
        ('GUARDIAN', 'LRP-B5', 'LRPB5', 'Serum La Roche-Posay B5+', 'San pham cham soc da phu hop da nhay cam.', 'La Roche-Posay', 'beauty', 'VND', 780000::numeric, 19::numeric, 77::numeric)
),
expanded_deals AS (
    SELECT
        b.source_code,
        b.product_sku,
        b.source_seed,
        b.title,
        b.description,
        b.brand,
        b.category,
        b.currency,
        b.base_original_price,
        b.base_discount_percent,
        b.base_score,
        gs.cycle_index
    FROM seed_deal_base b
    CROSS JOIN generate_series(0, 3) AS gs(cycle_index)
),
resolved_deals AS (
    SELECT
        s.id AS source_id,
        p.id AS product_id,
        format('SEED-%s-%s', e.source_seed, e.cycle_index + 1) AS source_deal_id,
        e.title || CASE WHEN e.cycle_index > 0 THEN format(' - Dot %s', e.cycle_index + 1) ELSE '' END AS title,
        e.description || CASE WHEN e.cycle_index > 0 THEN format(' (Cap nhat dot %s)', e.cycle_index + 1) ELSE '' END AS description,
        e.brand,
        e.category,
        format('%s/deals/%s-%s', COALESCE(s.base_url, 'https://hotdeal.vn'), e.source_seed, e.cycle_index + 1) AS external_url,
        p.image_url AS image_url,
        e.currency,
        ROUND((e.base_original_price * (1 + ((e.cycle_index % 3) - 1) * 0.04))::numeric, 2) AS original_price,
        LEAST(74::numeric, GREATEST(10::numeric, e.base_discount_percent + e.cycle_index * 2)) AS discount_percent,
        LEAST(99::numeric, e.base_score + e.cycle_index * 1.5) AS deal_score,
        (CURRENT_TIMESTAMP - INTERVAL '14 days' + make_interval(days => e.cycle_index))::timestamptz AS first_seen_at,
        (CURRENT_TIMESTAMP - INTERVAL '2 days' + make_interval(hours => e.cycle_index * 4))::timestamptz AS last_seen_at,
        (CURRENT_TIMESTAMP - INTERVAL '1 day')::timestamptz AS valid_from,
        (CURRENT_TIMESTAMP + INTERVAL '45 days' + make_interval(days => e.cycle_index))::timestamptz AS valid_until,
        jsonb_build_object(
            'seed', 'v4',
            'group', 'homepage-deal',
            'cycleIndex', e.cycle_index
        ) AS metadata
    FROM expanded_deals e
    JOIN source s ON s.code = e.source_code
    JOIN product p ON p.canonical_sku = e.product_sku
),
resolved_deals_with_price AS (
    SELECT
        rd.*,
        ROUND((rd.original_price * (100 - rd.discount_percent) / 100)::numeric, 2) AS deal_price
    FROM resolved_deals rd
)
INSERT INTO deal (
    source_id,
    product_id,
    source_deal_id,
    fingerprint,
    dedupe_key,
    title,
    normalized_title,
    description,
    brand,
    category,
    external_url,
    image_url,
    currency,
    original_price,
    deal_price,
    discount_percent,
    coupon_code,
    deal_score,
    status,
    valid_from,
    valid_until,
    first_seen_at,
    last_seen_at,
    metadata
)
SELECT
    rd.source_id,
    rd.product_id,
    rd.source_deal_id,
    md5(rd.source_deal_id || '-fp'),
    md5(rd.title || '-' || rd.source_id),
    rd.title,
    LOWER(REGEXP_REPLACE(rd.title, '\s+', ' ', 'g')),
    rd.description,
    rd.brand,
    rd.category,
    rd.external_url,
    rd.image_url,
    rd.currency,
    rd.original_price,
    rd.deal_price,
    rd.discount_percent,
    NULL,
    rd.deal_score,
    'ACTIVE',
    rd.valid_from,
    rd.valid_until,
    rd.first_seen_at,
    rd.last_seen_at,
    rd.metadata
FROM resolved_deals_with_price rd
WHERE NOT EXISTS (
    SELECT 1
    FROM deal existing
    WHERE existing.source_id = rd.source_id
      AND existing.source_deal_id = rd.source_deal_id
);

WITH seed_coupon_base (
    source_code,
    source_seed,
    title,
    description,
    coupon_code,
    discount_percent,
    valid_days
) AS (
    VALUES
        ('SHOPEE', 'SHOPEE10K', 'Ma giam gia Shopee 10K', 'Giam 10K cho don tu 99K.', 'SHOPEE10K', 10::numeric, 18),
        ('LAZADA', 'LAZADA50K', 'Ma giam gia Lazada 50K', 'Giam 50K cho don tu 500K.', 'LAZADA50K', 12::numeric, 15),
        ('TIKI', 'TIKI30K', 'Ma giam gia Tiki 30K', 'Giam 30K cho don tu 299K.', 'TIKI30K', 9::numeric, 12),
        ('SHOPEE', 'APPONLY25', 'Voucher App Only 25K', 'Giam 25K khi dat hang tren app.', 'APPONLY25', 14::numeric, 20),
        ('LAZADA', 'BANK8PCT', 'Hoan tien ngan hang 8%', 'Hoan 8 phan tram toi da 120K.', 'BANK8PCT', 8::numeric, 25),
        ('TIKI', 'SIEUSALE', 'Voucher Sieu Sale', 'Giam den 120K cho san pham chon loc.', 'SIEUSALE', 16::numeric, 10),
        ('SHOPEE', 'MEGA88', 'Voucher Mega 88K', 'Voucher 88K cho don tu 888K.', 'MEGA88', 11::numeric, 14),
        ('LAZADA', 'FREESHIP0D', 'Ma Freeship Toan Quoc', 'Mien phi van chuyen khong gioi han khu vuc.', 'FREESHIP0D', 7::numeric, 30)
),
expanded_coupon AS (
    SELECT
        cb.source_code,
        cb.source_seed,
        cb.title,
        cb.description,
        cb.coupon_code,
        cb.discount_percent,
        cb.valid_days,
        gs.cycle_index
    FROM seed_coupon_base cb
    CROSS JOIN generate_series(0, 1) AS gs(cycle_index)
),
resolved_coupon AS (
    SELECT
        s.id AS source_id,
        format('COUPON-%s-%s', ec.source_seed, ec.cycle_index + 1) AS source_deal_id,
        ec.title || CASE WHEN ec.cycle_index = 1 THEN ' - Dot 2' ELSE '' END AS title,
        ec.description,
        ec.coupon_code || CASE WHEN ec.cycle_index = 1 THEN 'B' ELSE '' END AS coupon_code,
        LEAST(70::numeric, ec.discount_percent + ec.cycle_index * 2) AS discount_percent,
        (CURRENT_TIMESTAMP - INTERVAL '8 days' + make_interval(days => ec.cycle_index))::timestamptz AS first_seen_at,
        (CURRENT_TIMESTAMP - INTERVAL '6 hours' + make_interval(hours => ec.cycle_index * 2))::timestamptz AS last_seen_at,
        (CURRENT_TIMESTAMP - INTERVAL '1 day')::timestamptz AS valid_from,
        (CURRENT_TIMESTAMP + make_interval(days => ec.valid_days + ec.cycle_index * 5))::timestamptz AS valid_until,
        format('%s/voucher/%s', COALESCE(s.base_url, 'https://hotdeal.vn'), lower(ec.coupon_code)) AS external_url
    FROM expanded_coupon ec
    JOIN source s ON s.code = ec.source_code
)
INSERT INTO deal (
    source_id,
    product_id,
    source_deal_id,
    fingerprint,
    dedupe_key,
    title,
    normalized_title,
    description,
    brand,
    category,
    external_url,
    image_url,
    currency,
    original_price,
    deal_price,
    discount_percent,
    coupon_code,
    deal_score,
    status,
    valid_from,
    valid_until,
    first_seen_at,
    last_seen_at,
    metadata
)
SELECT
    rc.source_id,
    NULL,
    rc.source_deal_id,
    md5(rc.source_deal_id || '-fp'),
    md5(rc.source_deal_id || '-dk'),
    rc.title,
    LOWER(REGEXP_REPLACE(rc.title, '\s+', ' ', 'g')),
    rc.description,
    NULL,
    'voucher',
    rc.external_url,
    NULL,
    'VND',
    NULL,
    0::numeric,
    rc.discount_percent,
    rc.coupon_code,
    70::numeric + rc.discount_percent,
    'ACTIVE',
    rc.valid_from,
    rc.valid_until,
    rc.first_seen_at,
    rc.last_seen_at,
    jsonb_build_object(
        'seed', 'v4',
        'group', 'homepage-coupon'
    )
FROM resolved_coupon rc
WHERE NOT EXISTS (
    SELECT 1
    FROM deal existing
    WHERE existing.source_id = rc.source_id
      AND existing.source_deal_id = rc.source_deal_id
);

WITH seeded_deals AS (
    SELECT
        d.id AS deal_id,
        d.product_id,
        d.source_id,
        d.currency,
        d.original_price,
        d.deal_price,
        d.discount_percent
    FROM deal d
    JOIN source s ON s.id = d.source_id
    WHERE d.source_deal_id LIKE 'SEED-%'
       OR d.source_deal_id LIKE 'COUPON-%'
),
history_points AS (
    SELECT
        sd.deal_id,
        sd.product_id,
        sd.source_id,
        sd.currency,
        sd.original_price,
        sd.deal_price,
        sd.discount_percent,
        gs.day_offset
    FROM seeded_deals sd
    CROSS JOIN generate_series(0, 2) AS gs(day_offset)
)
INSERT INTO price_history (
    deal_id,
    product_id,
    source_id,
    captured_at,
    currency,
    original_price,
    deal_price,
    discount_percent,
    availability_status
)
SELECT
    hp.deal_id,
    hp.product_id,
    hp.source_id,
    (CURRENT_TIMESTAMP - make_interval(days => 2 - hp.day_offset))::timestamptz AS captured_at,
    hp.currency,
    hp.original_price,
    ROUND(
        CASE
            WHEN hp.deal_price = 0 THEN 0
            ELSE hp.deal_price * (1 + (2 - hp.day_offset) * 0.015)
        END
    , 2) AS deal_price,
    hp.discount_percent,
    'IN_STOCK'
FROM history_points hp
ON CONFLICT (deal_id, captured_at)
DO UPDATE SET
    original_price = EXCLUDED.original_price,
    deal_price = EXCLUDED.deal_price,
    discount_percent = EXCLUDED.discount_percent,
    availability_status = EXCLUDED.availability_status,
    updated_at = NOW();
