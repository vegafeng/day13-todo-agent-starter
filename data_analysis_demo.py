#!/usr/bin/env python3
"""
MotherDuck 数据分析演示脚本
这个脚本展示了如何使用 MotherDuck (云端 DuckDB) 进行数据分析
"""

import duckdb
import pandas as pd
import json
from datetime import datetime

# MotherDuck 连接令牌 (从您的配置中获取)
MOTHERDUCK_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRvaHVhbmdqaWFAZ21haWwuY29tIiwic2Vzc2lvbiI6InRvaHVhbmdqaWEuZ21haWwuY29tIiwicGF0IjoiLUFvRmlRcE9xREZNb05sVFdwZzJha28yMDNnc0tkM3VyMXhBeHRKS3phZyIsInVzZXJJZCI6ImU0ZmUwZTYxLTgxMDEtNDdlZC05OGNhLTJmNGQ2MjZkYTUxYyIsImlzcyI6Im1kX3BhdCIsInJlYWRPbmx5IjpmYWxzZSwidG9rZW5UeXBlIjoicmVhZF93cml0ZSIsImlhdCI6MTc0Nzc0MTUxOX0.kmAvQ2AllpYo9UdotsqaysLHfe_yU51EeOpXYd85bkc"

def connect_to_motherduck():
    """连接到 MotherDuck"""
    try:
        # 使用令牌连接到 MotherDuck
        conn = duckdb.connect(f'md:?motherduck_token={MOTHERDUCK_TOKEN}')
        print("✅ 成功连接到 MotherDuck!")
        return conn
    except Exception as e:
        print(f"❌ 连接 MotherDuck 失败: {e}")
        # 如果连接失败，使用本地 DuckDB
        print("🔄 改用本地 DuckDB 进行演示...")
        return duckdb.connect(':memory:')

def create_sample_data(conn):
    """创建示例数据用于分析"""
    print("\n📊 创建示例数据...")
    
    # 创建销售数据表
    conn.execute("""
        CREATE TABLE IF NOT EXISTS sales_data (
            id INTEGER,
            product_name VARCHAR,
            category VARCHAR,
            sales_amount DECIMAL(10,2),
            sales_date DATE,
            region VARCHAR,
            customer_id INTEGER
        )
    """)
    
    # 插入示例数据
    sample_data = [
        (1, '智能手机', '电子产品', 2999.99, '2024-01-15', '北京', 1001),
        (2, '笔记本电脑', '电子产品', 5999.99, '2024-01-16', '上海', 1002),
        (3, '无线耳机', '电子产品', 299.99, '2024-01-17', '广州', 1003),
        (4, '运动鞋', '服装', 599.99, '2024-01-18', '深圳', 1004),
        (5, 'T恤衫', '服装', 99.99, '2024-01-19', '杭州', 1005),
        (6, '咖啡机', '家电', 1299.99, '2024-01-20', '成都', 1006),
        (7, '智能手表', '电子产品', 1999.99, '2024-01-21', '北京', 1007),
        (8, '护肤套装', '美容', 399.99, '2024-01-22', '上海', 1008),
        (9, '运动背包', '服装', 199.99, '2024-01-23', '广州', 1009),
        (10, '智能音箱', '电子产品', 399.99, '2024-01-24', '深圳', 1010)
    ]
    
    conn.executemany("""
        INSERT INTO sales_data VALUES (?, ?, ?, ?, ?, ?, ?)
    """, sample_data)
    
    print("✅ 示例数据创建完成!")

def analyze_sales_data(conn):
    """执行各种数据分析查询"""
    print("\n🔍 开始数据分析...")
    
    analyses = [
        {
            "title": "📈 总销售额统计",
            "query": """
                SELECT 
                    COUNT(*) as 订单数量,
                    SUM(sales_amount) as 总销售额,
                    AVG(sales_amount) as 平均订单金额,
                    MIN(sales_amount) as 最小订单金额,
                    MAX(sales_amount) as 最大订单金额
                FROM sales_data
            """
        },
        {
            "title": "🏷️ 按品类分析销售情况",
            "query": """
                SELECT 
                    category as 品类,
                    COUNT(*) as 订单数量,
                    SUM(sales_amount) as 销售额,
                    AVG(sales_amount) as 平均单价,
                    ROUND(SUM(sales_amount) * 100.0 / (SELECT SUM(sales_amount) FROM sales_data), 2) as 销售占比
                FROM sales_data
                GROUP BY category
                ORDER BY 销售额 DESC
            """
        },
        {
            "title": "🌍 按地区分析销售情况",
            "query": """
                SELECT 
                    region as 地区,
                    COUNT(*) as 订单数量,
                    SUM(sales_amount) as 销售额,
                    AVG(sales_amount) as 平均单价
                FROM sales_data
                GROUP BY region
                ORDER BY 销售额 DESC
            """
        },
        {
            "title": "📅 按日期分析销售趋势",
            "query": """
                SELECT 
                    sales_date as 销售日期,
                    COUNT(*) as 订单数量,
                    SUM(sales_amount) as 当日销售额,
                    STRING_AGG(product_name, ', ') as 销售产品
                FROM sales_data
                GROUP BY sales_date
                ORDER BY sales_date
            """
        },
        {
            "title": "🏆 热销产品 TOP 5",
            "query": """
                SELECT 
                    product_name as 产品名称,
                    category as 品类,
                    sales_amount as 销售金额,
                    region as 销售地区,
                    sales_date as 销售日期
                FROM sales_data
                ORDER BY sales_amount DESC
                LIMIT 5
            """
        }
    ]
    
    for analysis in analyses:
        print(f"\n{analysis['title']}")
        print("=" * 50)
        
        try:
            result = conn.execute(analysis['query']).fetchdf()
            if not result.empty:
                print(result.to_string(index=False))
            else:
                print("暂无数据")
        except Exception as e:
            print(f"查询执行错误: {e}")

def advanced_analytics(conn):
    """高级数据分析功能"""
    print("\n🚀 高级数据分析...")
    
    # 创建数据透视表
    print("\n📋 销售数据透视表 (品类 vs 地区)")
    print("=" * 50)
    
    try:
        pivot_query = """
            PIVOT sales_data
            ON region
            USING SUM(sales_amount) as 销售额
            GROUP BY category
            ORDER BY category
        """
        result = conn.execute(pivot_query).fetchdf()
        print(result.to_string(index=False))
    except Exception as e:
        print(f"数据透视表生成失败: {e}")
        
        # 备选方案：手动创建透视表
        try:
            manual_pivot = """
                SELECT 
                    category as 品类,
                    SUM(CASE WHEN region = '北京' THEN sales_amount ELSE 0 END) as 北京,
                    SUM(CASE WHEN region = '上海' THEN sales_amount ELSE 0 END) as 上海,
                    SUM(CASE WHEN region = '广州' THEN sales_amount ELSE 0 END) as 广州,
                    SUM(CASE WHEN region = '深圳' THEN sales_amount ELSE 0 END) as 深圳,
                    SUM(CASE WHEN region = '杭州' THEN sales_amount ELSE 0 END) as 杭州,
                    SUM(CASE WHEN region = '成都' THEN sales_amount ELSE 0 END) as 成都,
                    SUM(sales_amount) as 总计
                FROM sales_data
                GROUP BY category
                ORDER BY 总计 DESC
            """
            result = conn.execute(manual_pivot).fetchdf()
            print(result.to_string(index=False))
        except Exception as e2:
            print(f"手动透视表也失败: {e2}")
    
    # 时间序列分析
    print("\n📈 时间序列分析")
    print("=" * 50)
    
    try:
        time_series_query = """
            SELECT 
                sales_date,
                sales_amount,
                SUM(sales_amount) OVER (ORDER BY sales_date) as 累计销售额,
                AVG(sales_amount) OVER (ORDER BY sales_date ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) as 三日移动平均
            FROM sales_data
            ORDER BY sales_date
        """
        result = conn.execute(time_series_query).fetchdf()
        print(result.to_string(index=False))
    except Exception as e:
        print(f"时间序列分析失败: {e}")

def export_results(conn):
    """导出分析结果"""
    print("\n💾 导出分析结果...")
    
    try:
        # 导出到 CSV
        conn.execute("""
            COPY (
                SELECT 
                    category as 品类,
                    COUNT(*) as 订单数量,
                    SUM(sales_amount) as 销售额,
                    AVG(sales_amount) as 平均单价
                FROM sales_data
                GROUP BY category
                ORDER BY 销售额 DESC
            ) TO 'sales_analysis_by_category.csv' (HEADER, DELIMITER ',')
        """)
        print("✅ 数据已导出到 sales_analysis_by_category.csv")
        
        # 导出到 JSON
        result = conn.execute("""
            SELECT 
                category,
                COUNT(*) as order_count,
                SUM(sales_amount) as total_sales,
                AVG(sales_amount) as avg_price
            FROM sales_data
            GROUP BY category
            ORDER BY total_sales DESC
        """).fetchdf()
        
        result.to_json('sales_analysis.json', orient='records', indent=2)
        print("✅ 数据已导出到 sales_analysis.json")
        
    except Exception as e:
        print(f"导出失败: {e}")

def show_database_info(conn):
    """显示数据库信息"""
    print("\n📊 数据库信息")
    print("=" * 50)
    
    try:
        # 显示所有表
        tables = conn.execute("SHOW TABLES").fetchdf()
        print("数据库中的表:")
        print(tables.to_string(index=False))
        
        # 显示表结构
        print("\n表结构信息:")
        describe = conn.execute("DESCRIBE sales_data").fetchdf()
        print(describe.to_string(index=False))
        
        # 显示数据库版本
        version = conn.execute("SELECT version()").fetchone()
        print(f"\nDuckDB 版本: {version[0]}")
        
    except Exception as e:
        print(f"获取数据库信息失败: {e}")

def main():
    """主函数"""
    print("🦆 MotherDuck 数据分析演示")
    print("=" * 60)
    
    # 连接数据库
    conn = connect_to_motherduck()
    
    try:
        # 显示数据库信息
        show_database_info(conn)
        
        # 创建示例数据
        create_sample_data(conn)
        
        # 执行基础数据分析
        analyze_sales_data(conn)
        
        # 执行高级数据分析
        advanced_analytics(conn)
        
        # 导出结果
        export_results(conn)
        
        print("\n🎉 数据分析完成!")
        print("\n💡 您可以根据需要修改查询语句进行更深入的分析")
        
    except Exception as e:
        print(f"❌ 执行过程中出现错误: {e}")
    
    finally:
        conn.close()
        print("\n🔐 数据库连接已关闭")

if __name__ == "__main__":
    main()
