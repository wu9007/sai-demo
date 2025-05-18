create table vector_store
(
    id        uuid default uuid_generate_v4() not null
        primary key,
    content   text,
    metadata  json,
    embedding vector(1024)
);

create index vector_store_embedding_idx
    on vector_store using hnsw (embedding vector_cosine_ops);

create table med_report_info
(
    med_report_id varchar(32)   not null,
    indicator     varchar(3000) not null,
    symptom       varchar(500),
    create_time   timestamp     not null,
    alter_time    timestamp     not null,
    file_path     varchar(225)  not null
);

comment
on column med_report_info.med_report_id is ''主键'';

comment
on column med_report_info.indicator is ''原始指标'';

comment
on column med_report_info.symptom is ''推断症状'';

comment
on column med_report_info.create_time is ''创建时间'';

comment
on column med_report_info.alter_time is ''末次调整时间'';

comment
on column med_report_info.file_path is ''原始文件路径'';

